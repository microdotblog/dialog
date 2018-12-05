package com.dialogapp.dialog.repository

import androidx.annotation.MainThread
import androidx.paging.PagedList
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.vo.MicroBlogResponse
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.util.PagingRequestHelper
import com.dialogapp.dialog.util.createStatusLiveData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
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
        private val handleResponse: (String, MicroBlogResponse?) -> Unit,
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
                    count = networkPageSize)
                    .enqueue(createWebserviceCallback(it))
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
                    count = networkPageSize)
                    .enqueue(createWebserviceCallback(it))
        }
    }

    /**
     * every time it gets new items, boundary callback simply inserts them into the database and
     * paging library takes care of refreshing the list if necessary.
     */
    private fun insertItemsIntoDb(
            response: Response<MicroBlogResponse>,
            it: PagingRequestHelper.Request.Callback) {
        ioExecutor.execute {
            handleResponse(endpoint, response.body())
            it.recordSuccess()
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: Post) {
        // ignored, since we only ever append to what's in the DB
    }

    private fun createWebserviceCallback(it: PagingRequestHelper.Request.Callback)
            : Callback<MicroBlogResponse> {
        return object : Callback<MicroBlogResponse> {
            override fun onFailure(
                    call: Call<MicroBlogResponse>,
                    t: Throwable) {
                it.recordFailure(t)
            }

            override fun onResponse(
                    call: Call<MicroBlogResponse>,
                    response: Response<MicroBlogResponse>) {
                insertItemsIntoDb(response, it)
            }
        }
    }
}