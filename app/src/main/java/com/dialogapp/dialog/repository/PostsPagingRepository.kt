package com.dialogapp.dialog.repository

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.paging.toLiveData
import com.dialogapp.dialog.AppExecutors
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.model.MicroBlogResponse
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.vo.Listing
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

class PostsPagingRepository @Inject constructor(private val microBlogDb: MicroBlogDb,
                                                private val microblogService: MicroblogService,
                                                private val appExecutors: AppExecutors) {
    companion object {
        private const val DEFAULT_NETWORK_PAGE_SIZE = 10
    }

    private var networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE

    fun postsOfEndpoint(endpoint: String, networkPageSize: Int = DEFAULT_NETWORK_PAGE_SIZE): Listing<Post> {
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

        return Listing(
                pagedList = livePagedList,
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

    private fun insertPostsIntoDb(endpoint: String, microBlogResponse: MicroBlogResponse?) {
        microBlogResponse!!.posts.let { posts ->
            microBlogDb.runInTransaction {
                posts.map {
                    it.endpoint = endpoint
                    it
                }
                microBlogDb.posts().insertPosts(posts)
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
        microblogService.getEndpoint(endpoint, null, this.networkPageSize).enqueue(
                object : Callback<MicroBlogResponse> {
                    override fun onFailure(call: Call<MicroBlogResponse>, t: Throwable) {
                        // retrofit calls this on main thread so safe to call set value
                        networkState.value = NetworkState.error(t.message)
                    }

                    override fun onResponse(call: Call<MicroBlogResponse>,
                                            response: Response<MicroBlogResponse>) {
                        appExecutors.diskIO().execute {
                            microBlogDb.runInTransaction {
                                microBlogDb.posts().deletePostsByEndpoint(endpoint)
                                insertPostsIntoDb(endpoint, response.body())
                            }
                            // since we are in bg thread now, post the result.
                            networkState.postValue(NetworkState.LOADED)
                        }
                    }
                }
        )
        return networkState
    }
}