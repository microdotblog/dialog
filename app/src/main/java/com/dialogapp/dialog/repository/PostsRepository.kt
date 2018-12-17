package com.dialogapp.dialog.repository

import androidx.collection.ArrayMap
import androidx.lifecycle.LiveData
import com.dialogapp.dialog.AppExecutors
import com.dialogapp.dialog.api.ApiResponse
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.vo.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostsRepository @Inject constructor(private val appExecutors: AppExecutors,
                                          private val microblogService: MicroblogService) {

    private val endpointRateLimit = RateLimiter<String>(2, TimeUnit.MINUTES)
    private val listings = ArrayMap<String, List<Post>>()
    private var endpointData = ArrayMap<String, EndpointData>()

    fun loadDiscover(topic: String?, refresh: Boolean): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData == null || data.postData.isEmpty() ||
                        (endpointRateLimit.shouldFetch(topic ?: DISCOVER) && refresh)
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

    fun loadPostsByUsername(username: String, refresh: Boolean): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData == null || data.postData.isEmpty() ||
                        (endpointRateLimit.shouldFetch(username) && refresh)
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getPostsByUsername(username)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                endpointData[username] = EndpointData(endpoint = username,
                        microblog = item.microblog, author = item.author)
                listings[username] = item.posts
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return object : LiveData<Listing<Post>>() {
                    override fun onActive() {
                        super.onActive()
                        value = Listing(endpointData = endpointData[username],
                                postData = listings[username])
                    }
                }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(username)
            }
        }.asLiveData()
    }

    fun loadConversation(convId: String, refresh: Boolean): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData == null || data.postData.isEmpty() ||
                        (endpointRateLimit.shouldFetch(convId) && refresh)
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getConversation(convId)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                endpointData[convId] = EndpointData(endpoint = convId,
                        microblog = item.microblog, author = item.author)
                listings[convId] = item.posts
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return object : LiveData<Listing<Post>>() {
                    override fun onActive() {
                        super.onActive()
                        value = Listing(endpointData = endpointData[convId],
                                postData = listings[convId])
                    }
                }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(convId)
            }
        }.asLiveData()
    }

    fun loadFavorites(refresh: Boolean): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData == null || data.postData.isEmpty() ||
                        (endpointRateLimit.shouldFetch(FAVORITES) && refresh)
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getFavorites()
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                endpointData[FAVORITES] = EndpointData(endpoint = FAVORITES,
                        microblog = item.microblog, author = item.author)
                listings[FAVORITES] = item.posts
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return object : LiveData<Listing<Post>>() {
                    override fun onActive() {
                        super.onActive()
                        value = Listing(endpointData = endpointData[FAVORITES],
                                postData = listings[FAVORITES])
                    }
                }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(FAVORITES)
            }
        }.asLiveData()
    }
}
