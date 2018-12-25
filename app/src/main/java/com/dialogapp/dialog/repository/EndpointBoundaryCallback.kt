package com.dialogapp.dialog.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.dialogapp.dialog.api.*
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.util.PagingRequestHelper
import com.dialogapp.dialog.util.calladapters.ApiResponseCallback
import com.dialogapp.dialog.util.createStatusLiveData
import com.dialogapp.dialog.vo.MENTIONS
import com.dialogapp.dialog.vo.MicroBlogResponse
import timber.log.Timber
import java.util.concurrent.Executor

/**
 * This boundary callback gets notified when user reaches to the edges of the list such that the
 * database cannot provide any more data.
 * <p>
 * The boundary callback might be called multiple times for the same direction so it does its own
 * rate limiting using the PagingRequestHelper class.
 */
class EndpointBoundaryCallback(
        private val webservice: MicroblogService,
        private val endpoint: String,
        private val handleResponse: (String, MicroBlogResponse, Boolean) -> Unit,
        private val ioExecutor: Executor,
        private val networkPageSize: Int)
    : PagedList.BoundaryCallback<Post>() {

    val helper = PagingRequestHelper(ioExecutor)
    val networkState = helper.createStatusLiveData()

    /**
     * Database returned 0 items. We should query the backend for more items.
     */
    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {

            webservice.getEndpoint(
                    endpoint = endpoint,
                    beforeId = null,
                    count = if (endpoint == MENTIONS) null else networkPageSize) //TODO: Remove check after api support
                    .enqueue(createWebserviceCallback(it, true))
        }
    }

    /**
     * User reached to the end of the list.
     */
    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: Post) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getEndpoint(
                    endpoint = endpoint,
                    beforeId = itemAtEnd.id,
                    count = if (endpoint == MENTIONS) null else networkPageSize) //TODO: Remove check after api support
                    .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
            response: MicroBlogResponse,
            it: PagingRequestHelper.Request.Callback,
            setTimeStamp: Boolean) {
        ioExecutor.execute {
            handleResponse(endpoint, response, setTimeStamp)
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Post) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback,
                                         setTimeStamp: Boolean = false)
            : ApiResponseCallback<MicroBlogResponse> {
        return object : ApiResponseCallback<MicroBlogResponse> {
            override fun onSuccess(response: ApiResponse<MicroBlogResponse>) {
                when (response) {
                    is ApiSuccessResponse -> insertItemsIntoDb(response.body, it, setTimeStamp)
                    is ApiEmptyResponse -> {
                        Timber.e("Received empty response for endpoint: %s", endpoint)
                        it.recordFailure(Throwable("Received empty response"))
                    }
                    is ApiErrorResponse -> {
                        Timber.e("Received malformed response for: %s, message: %s",
                                endpoint, response.errorMessage)
                        it.recordFailure(Throwable("Received malformed response"))
                    }
                }
            }

            override fun onFailure(response: ApiErrorResponse<MicroBlogResponse>) {
                Timber.d("Request failed: %s", response.errorMessage)
                it.recordFailure(Throwable(response.errorMessage))
            }
        }
    }
}