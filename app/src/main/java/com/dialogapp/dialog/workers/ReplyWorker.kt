package com.dialogapp.dialog.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.dialogapp.dialog.api.MicroblogService
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class ReplyWorker(appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var microblogService: MicroblogService

    companion object {
        private const val WORKER_KEY_ID = "id"
        private const val WORKER_KEY_CONTENT = "content"

        fun createInputData(id: String?, content: String?): Data {
            val builder = Data.Builder()
            builder.putString(WORKER_KEY_ID, id)
            builder.putString(WORKER_KEY_CONTENT, content)
            return builder.build()
        }
    }

    override suspend fun doWork(): Result {
        val id = inputData.getString(WORKER_KEY_ID)
        val content = inputData.getString(WORKER_KEY_CONTENT)

        if (id != null && content != null) {
            try {
                val response = microblogService.replyToPost(id, content).await()
                if (response.isSuccessful) {
                    if (validResponse(response.body())) {
                        Timber.d("Sent reply successfully")
                        return Result.success()
                    }
                } else {
                    Timber.d(response.message())
                }
            } catch (throwable: Throwable) {
                Timber.d(throwable)
            }
        } else {
            Timber.e("Received null: id=$id content=$content")
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