package com.dialogapp.dialog.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.db.InMemoryDb
import com.dialogapp.dialog.db.MicroBlogDb
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class FollowWorker(appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var microblogService: MicroblogService

    @Inject
    lateinit var diskDb: MicroBlogDb

    @Inject
    lateinit var inMemDb: InMemoryDb

    companion object {
        private const val WORKER_KEY_USERNAME = "username"
        private const val WORKER_KEY_IS_FOLLOWING = "is_following"

        fun createInputData(username: String?, isFollowing: Boolean): Data {
            val builder = Data.Builder()
            builder.putString(WORKER_KEY_USERNAME, username)
            builder.putBoolean(WORKER_KEY_IS_FOLLOWING, isFollowing)
            return builder.build()
        }
    }

    override suspend fun doWork(): Result {
        val username = inputData.getString(WORKER_KEY_USERNAME)
        val isFollowing = inputData.getBoolean(WORKER_KEY_IS_FOLLOWING, false)

        if (username == null) {
            return Result.failure()
        } else {
            try {
                when (isFollowing) {
                    true -> {
                        val response = microblogService.unfollowUser(username).await()
                        if (response.isSuccessful) {
                            if (validResponse(response.body())) {
                                Timber.d("Unfollow request successful")
                                diskDb.followingData().removeAccount(username)
                                inMemDb.endpointData().updateFollowingState(username, false)
                                return Result.success()
                            }
                        } else {
                            Timber.d(response.message())
                        }
                    }
                    false -> {
                        val response = microblogService.followUser(username).await()
                        if (response.isSuccessful) {
                            if (validResponse(response.body())) {
                                Timber.d("Follow request successful")
                                inMemDb.endpointData().updateFollowingState(username, true)
                                return Result.success()
                            }
                        } else {
                            Timber.d(response.message())
                        }
                    }
                }
            } catch (throwable: Throwable) {
                Timber.d(throwable)
            }
        }
        return Result.failure()
    }

    private fun validResponse(body: ResponseBody?): Boolean {
        return try {
            body?.string().equals("{}")
        } catch (e: IOException) {
            Timber.e("Unexpected response : %s", e.message)
            false
        }
    }
}