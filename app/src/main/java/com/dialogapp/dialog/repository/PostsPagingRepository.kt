package com.dialogapp.dialog.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.dialogapp.dialog.AppExecutors
import com.dialogapp.dialog.api.*
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.util.calladapters.ApiResponseCallback
import com.dialogapp.dialog.vo.MENTIONS
import com.dialogapp.dialog.vo.MicroBlogResponse
import com.dialogapp.dialog.vo.PagedListing
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostsPagingRepository @Inject constructor(private val microBlogDb: MicroBlogDb,
                                                private val microblogService: MicroblogService,
                                                private val appExecutors: AppExecutors) {
    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 10
    }

    private var networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE
    private val endpointRateLimit = RateLimiter<String>(2, TimeUnit.MINUTES)

    fun postsOfEndpoint(endpoint: String, networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE): PagedListing<Post> {
        this.networkPageSize = networkPageSize
        // create a boundary callback which will observe when the user reaches to the edges of
        // the list and update the database with extra data.
        val boundaryCallback = EndpointBoundaryCallback(
                webservice = microblogService,
                endpoint = endpoint,
                handleResponse = this::insertPostsIntoDb,
                ioExecutor = appExecutors.diskIO(),
                networkPageSize = networkPageSize
        )
        // we are using a mutable live data to trigger refresh requests which eventually calls
        // refresh method and gets a new live data. Each refresh request by the user becomes a newly
        // dispatched data in refreshTrigger
        val refreshTrigger = MutableLiveData<Unit>()
        val refreshState = Transformations.switchMap(refreshTrigger) {
            refresh(endpoint)
        }

        // We use toLiveData Kotlin extension function here, you could also use LivePagedListBuilder
        val livePagedList = microBlogDb.posts().loadPostsByEndpoint(endpoint).toLiveData(
                pageSize = networkPageSize,
                boundaryCallback = boundaryCallback)

        val liveEndpointData = microBlogDb.posts().loadEndpointData(endpoint)

        return PagedListing(
                pagedList = livePagedList,
                endpointData = liveEndpointData,
                networkState = boundaryCallback.networkState,
                retry = {
                    boundaryCallback.helper.retryAllFailed()
                },
                refresh = {
                    refreshTrigger.value = null
                },
                refreshState = refreshState
        )
    }

    private fun insertPostsIntoDb(endpoint: String, microBlogResponse: MicroBlogResponse,
                                  setTimeStamp: Boolean) {
        microBlogResponse.let { data ->
            microBlogDb.runInTransaction {
                val endpointData = EndpointData(endpoint, data.microblog, data.author)
                endpointData.lastFetched = when {
                    setTimeStamp -> System.currentTimeMillis()
                    else -> microBlogDb.posts().fetchLastTimestamp(endpoint)
                }
                microBlogDb.posts().insertEndpointData(endpointData)

                data.posts.let { posts ->
                    posts.map {
                        it.belongsToEndpoint = endpoint
                        it
                    }
                    microBlogDb.posts().insertPosts(posts)
                }
            }
        }
    }

    /**
     * When refresh is called, we simply run a fresh network request and when it arrives, clear
     * the database table and insert all new items in a transaction.
     * <p>
     * Since the PagedList already uses a database bound data source, it will automatically be
     * updated after the database transaction is finished.
     */
    @MainThread
    private fun refresh(endpoint: String): LiveData<NetworkState> {
        val networkState = MutableLiveData<NetworkState>()
        networkState.value = NetworkState.LOADING
        //TODO: Remove check after api support
        val networkPageSize = when (endpoint) {
            MENTIONS -> null
            else -> this.networkPageSize
        }
        if (endpointRateLimit.shouldFetch(endpoint)) {
            microblogService.getEndpoint(endpoint, null, networkPageSize).enqueue(
                    object : ApiResponseCallback<MicroBlogResponse> {
                        override fun onSuccess(response: ApiResponse<MicroBlogResponse>) {
                            when (response) {
                                is ApiSuccessResponse -> appExecutors.diskIO().execute {
                                    microBlogDb.runInTransaction {
                                        microBlogDb.posts().deletePostsByEndpoint(endpoint)
                                        insertPostsIntoDb(endpoint, response.body, true)
                                    }
                                    networkState.postValue(NetworkState.LOADED)
                                }
                                is ApiEmptyResponse -> {
                                    Timber.e("Received empty response for endpoint: %s", endpoint)
                                    endpointRateLimit.reset(endpoint)
                                    networkState.postValue(NetworkState.error("Received empty response"))
                                }
                                is ApiErrorResponse -> {
                                    Timber.e("Received malformed response for: %s, message: %s",
                                            endpoint, response.errorMessage)
                                    endpointRateLimit.reset(endpoint)
                                    networkState.postValue(NetworkState.error("Received malformed response"))
                                }
                            }
                        }

                        override fun onFailure(response: ApiErrorResponse<MicroBlogResponse>) {
                            Timber.d("Request failed: %s", response.errorMessage)
                            endpointRateLimit.reset(endpoint)
                            networkState.postValue(NetworkState.error(response.errorMessage))
                        }
                    }
            )
        } else {
            networkState.value = NetworkState.LOADED
        }
        return networkState
    }
}