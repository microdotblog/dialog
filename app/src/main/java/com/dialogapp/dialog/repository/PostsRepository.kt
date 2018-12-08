package com.dialogapp.dialog.repository

import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import com.dialogapp.dialog.AppExecutors
import com.dialogapp.dialog.api.ApiResponse
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.vo.DISCOVER
import com.dialogapp.dialog.vo.Listing
import com.dialogapp.dialog.vo.MicroBlogResponse
import com.dialogapp.dialog.vo.Resource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostsRepository @Inject constructor(private val appExecutors: AppExecutors,
                                          private val microblogService: MicroblogService) {

    private val endpointRateLimit = RateLimiter<String>(2, TimeUnit.MINUTES)
    private val listings = ArrayMap<String, List<Post>>()
    private var endpointData = ArrayMap<String, EndpointData>()

    fun loadDiscover(topic: String?): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData == null || data.postData.isEmpty() ||
                        endpointRateLimit.shouldFetch(topic ?: DISCOVER)
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return if (topic == null)
                    microblogService.getFeaturedPosts()
                else
                    microblogService.getFeaturedPosts(topic)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                endpointData[topic] = EndpointData(endpoint = topic
                        ?: DISCOVER, microblog = item.microblog, author = item.author)
                listings[topic ?: DISCOVER] = item.posts
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return object : LiveData<Listing<Post>>() {
                    override fun onActive() {
                        super.onActive()
                        value = Listing(endpointData = endpointData[topic
                                ?: DISCOVER], postData = listings[topic ?: DISCOVER])
                    }
                }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(topic ?: DISCOVER)
            }
        }.asLiveData()
    }
}
