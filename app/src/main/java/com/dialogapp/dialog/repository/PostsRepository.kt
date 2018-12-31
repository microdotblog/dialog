package com.dialogapp.dialog.repository

import androidx.lifecycle.LiveData
import com.dialogapp.dialog.AppExecutors
import com.dialogapp.dialog.api.ApiResponse
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.db.InMemoryDb
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.model.EndpointData
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.util.combineAndCompute
import com.dialogapp.dialog.vo.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class PostsRepository @Inject constructor(private val appExecutors: AppExecutors,
                                          private val db: MicroBlogDb,
                                          private val inMemoryDb: InMemoryDb,
                                          private val microblogService: MicroblogService) {

    private val endpointRateLimit = RateLimiter<String>(2, TimeUnit.MINUTES)

    fun loadDiscover(topic: String?, refresh: Boolean): LiveData<Resource<Listing<Post>>> {
        return object : NetworkBoundResource<Listing<Post>, MicroBlogResponse>(appExecutors) {
            override fun shouldFetch(data: Listing<Post>?): Boolean {
                return data?.postData.isNullOrEmpty() ||
                        (refresh && endpointRateLimit.shouldFetch(topic ?: DISCOVER))
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return if (topic == null)
                    microblogService.getFeaturedPosts()
                else
                    microblogService.getFeaturedPosts(topic)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                inMemoryDb.runInTransaction {
                    val endpointData = EndpointData(endpoint = topic
                            ?: DISCOVER, microblog = item.microblog, author = item.author)
                    inMemoryDb.endpointData().insertEndpointData(endpointData)

                    item.posts.map {
                        it.belongsToEndpoint = topic ?: DISCOVER
                    }
                    inMemoryDb.posts().insertPosts(item.posts)
                }
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return inMemoryDb.posts().loadPostsByEndpointAsLiveData(endpoint = topic
                        ?: DISCOVER).combineAndCompute(inMemoryDb.endpointData().loadEndpointData(endpoint = topic
                        ?: DISCOVER)) { posts, endpointData ->
                    Listing(endpointData = endpointData, postData = posts)
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
                return data?.postData.isNullOrEmpty() ||
                        (refresh && endpointRateLimit.shouldFetch(username))
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getPostsByUsername(username)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                inMemoryDb.runInTransaction {
                    val endpointData = EndpointData(endpoint = username,
                            microblog = item.microblog, author = item.author)
                    inMemoryDb.endpointData().insertEndpointData(endpointData)

                    item.posts.map {
                        it.belongsToEndpoint = username
                    }
                    inMemoryDb.posts().insertPosts(item.posts)
                }
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return inMemoryDb.posts().loadPostsByEndpointAsLiveData(endpoint = username)
                        .combineAndCompute(inMemoryDb.endpointData().loadEndpointData(endpoint = username))
                        { posts, endpointData ->
                            Listing(endpointData = endpointData, postData = posts)
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
                return data?.postData.isNullOrEmpty() ||
                        (refresh && endpointRateLimit.shouldFetch(convId))
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getConversation(convId)
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                inMemoryDb.runInTransaction {
                    val endpointData = EndpointData(endpoint = convId,
                            microblog = item.microblog, author = item.author)
                    inMemoryDb.endpointData().insertEndpointData(endpointData)

                    item.posts.map {
                        it.belongsToEndpoint = convId
                    }
                    inMemoryDb.posts().insertPosts(item.posts)
                }
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return inMemoryDb.posts().loadPostsByEndpointAsLiveData(endpoint = convId)
                        .combineAndCompute(inMemoryDb.endpointData().loadEndpointData(endpoint = convId))
                        { posts, endpointData ->
                            Listing(endpointData = endpointData, postData = posts)
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
                return data?.postData.isNullOrEmpty() ||
                        (refresh && endpointRateLimit.shouldFetch(FAVORITES))
            }

            override fun createCall(): LiveData<ApiResponse<MicroBlogResponse>> {
                return microblogService.getFavorites()
            }

            override fun saveCallResult(item: MicroBlogResponse) {
                inMemoryDb.runInTransaction {
                    val endpointData = EndpointData(endpoint = FAVORITES,
                            microblog = item.microblog, author = item.author)
                    inMemoryDb.endpointData().insertEndpointData(endpointData)

                    item.posts.map {
                        it.belongsToEndpoint = FAVORITES
                    }
                    inMemoryDb.posts().insertPosts(item.posts)
                }
            }

            override fun loadFromDb(): LiveData<Listing<Post>> {
                return inMemoryDb.posts().loadPostsByEndpointAsLiveData(endpoint = FAVORITES)
                        .combineAndCompute(inMemoryDb.endpointData().loadEndpointData(endpoint = FAVORITES))
                        { posts, endpointData ->
                            Listing(endpointData = endpointData, postData = posts)
                        }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(FAVORITES)
            }
        }.asLiveData()
    }

    fun loadFollowing(username: String, isSelf: Boolean, refresh: Boolean): LiveData<Resource<List<FollowingAccount>>> {
        val endpoint = FOLLOWING.plus("_$username")
        return object : NetworkBoundResource<List<FollowingAccount>, List<FollowingAccount>>(appExecutors) {
            override fun shouldFetch(data: List<FollowingAccount>?): Boolean {
                return data == null || data.isEmpty() ||
                        (refresh && endpointRateLimit.shouldFetch(endpoint))
            }

            override fun createCall(): LiveData<ApiResponse<List<FollowingAccount>>> {
                return if (isSelf)
                    microblogService.getFollowingSelf(username)
                else
                    microblogService.getFollowing(username)
            }

            override fun saveCallResult(item: List<FollowingAccount>) {
                when {
                    isSelf -> {
                        db.runInTransaction {
                            val endpointData = EndpointData(endpoint = endpoint,
                                    microblog = null, author = null)
                            db.endpointData().insertEndpointData(endpointData)

                            item.map {
                                it.belongsToEndpoint = endpoint
                            }
                            db.followingData().insertFollowingAccounts(item)
                        }
                    }
                    else -> {
                        inMemoryDb.runInTransaction {
                            val endpointData = EndpointData(endpoint = endpoint,
                                    microblog = null, author = null)
                            inMemoryDb.endpointData().insertEndpointData(endpointData)

                            item.map {
                                it.belongsToEndpoint = endpoint
                            }
                            inMemoryDb.followingData().insertFollowingAccounts(item)
                        }
                    }
                }
            }

            override fun loadFromDb(): LiveData<List<FollowingAccount>> {
                return when {
                    isSelf -> db.followingData().loadFollowingAccounts(endpoint)
                    else -> inMemoryDb.followingData().loadFollowingAccounts(endpoint)
                }
            }

            override fun onFetchFailed() {
                endpointRateLimit.reset(endpoint)
            }
        }.asLiveData()
    }
}
