package com.dialogapp.dialog.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.db.InMemoryDb
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.vo.FAVORITES
import com.dialogapp.dialog.vo.MENTIONS
import com.dialogapp.dialog.vo.TIMELINE
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class FavoriteWorker(appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var microblogService: MicroblogService

    @Inject
    lateinit var diskDb: MicroBlogDb

    @Inject
    lateinit var inMemDb: InMemoryDb

    companion object {
        private const val WORKER_KEY_POST_ID = "id"
        private const val WORKER_KEY_BELONGS_TO = "belongs_to"

        fun createInputData(postId: String?, belongsToEndpoint: String?): Data {
            val builder = Data.Builder()
            builder.putString(WORKER_KEY_POST_ID, postId)
            builder.putString(WORKER_KEY_BELONGS_TO, belongsToEndpoint)
            return builder.build()
        }
    }

    override suspend fun doWork(): Result {
        val postId = inputData.getString(WORKER_KEY_POST_ID)
        val belongsTo = inputData.getString(WORKER_KEY_BELONGS_TO)

        if (postId == null || belongsTo == null) {
            return Result.failure()
        } else {
            try {
                val favState = when {
                    (belongsTo == TIMELINE || belongsTo == MENTIONS) -> {
                        diskDb.posts().getFavoriteState(belongsTo, postId)
                    }
                    else -> {
                        inMemDb.posts().getFavoriteState(belongsTo, postId)
                    }
                }

                when (favState) {
                    true -> {
                        val response = microblogService.unfavoritePost(postId).await()
                        if (response.isSuccessful) {
                            if (validResponse(response.body())) {
                                Timber.d("Unfavorite request successful")
                                val username: String
                                val datePublished: String
                                when (belongsTo) {
                                    FAVORITES -> {
                                        username = inMemDb.posts().getUsername(belongsTo, postId)
                                        datePublished = inMemDb.posts().getDate(belongsTo, postId)

                                        diskDb.posts().updateFavoriteStatesGiven(username = username,
                                                datePublished = datePublished, state = false)
                                        inMemDb.posts().updateAndRemoveGivenFavId(favId = postId,
                                                state = false)
                                    }
                                    else -> {
                                        when {
                                            (belongsTo == TIMELINE || belongsTo == MENTIONS) -> {
                                                username = diskDb.posts().getUsername(belongsTo, postId)
                                                datePublished = diskDb.posts().getDate(belongsTo, postId)
                                            }
                                            else -> {
                                                username = inMemDb.posts().getUsername(belongsTo, postId)
                                                datePublished = inMemDb.posts().getDate(belongsTo, postId)
                                            }
                                        }

                                        inMemDb.posts().updateAndRemoveFromFav(username = username,
                                                datePublished = datePublished, postId = postId, state = false)
                                        diskDb.posts().updateFavoriteState(postId = postId, state = false)
                                    }
                                }
                                return Result.success()
                            }
                        } else {
                            Timber.d(response.message())
                        }
                    }
                    false -> {
                        val response = microblogService.favoritePost(postId).await()
                        if (response.isSuccessful) {
                            if (validResponse(response.body())) {
                                Timber.d("Favorite request successful")
                                diskDb.posts().updateFavoriteState(postId, true)
                                inMemDb.posts().updateFavoriteState(postId, true)
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