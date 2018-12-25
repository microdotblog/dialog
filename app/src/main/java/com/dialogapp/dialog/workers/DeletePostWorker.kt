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

class DeletePostWorker(appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var microblogService: MicroblogService

    @Inject
    lateinit var diskDb: MicroBlogDb

    @Inject
    lateinit var inMemDb: InMemoryDb

    companion object {
        private const val WORKER_KEY_POST_ID = "id"

        fun createInputData(postId: String): Data {
            val builder = Data.Builder()
            builder.putString(WORKER_KEY_POST_ID, postId)
            return builder.build()
        }
    }

    override suspend fun doWork(): Result {
        val postId = inputData.getString(WORKER_KEY_POST_ID)

        if (postId != null) {
            try {
                val response = microblogService.deletePost(postId).await()
                if (response.isSuccessful) {
                    if (validResponse(response.body())) {
                        Timber.d("Delete request successful for id : $postId")
                        diskDb.posts().deletePost(postId)
                        inMemDb.posts().deletePost(postId)
                        return Result.success()
                    }
                } else {
                    Timber.d(response.message())
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