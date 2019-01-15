package com.dialogapp.dialog.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import com.dialogapp.dialog.api.MicroblogService
import com.dialogapp.dialog.vo.ErrorResponse
import com.squareup.moshi.Moshi
import okhttp3.ResponseBody
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject


class NewPostWorker(appContext: Context, workerParameters: WorkerParameters)
    : CoroutineWorker(appContext, workerParameters) {

    @Inject
    lateinit var microblogService: MicroblogService

    companion object {
        private const val WORKER_KEY_TITLE = "title"
        private const val WORKER_KEY_CONTENT = "content"

        fun createInputData(title: String?, content: String?): Data {
            val builder = Data.Builder()
            builder.putString(WORKER_KEY_TITLE, title)
            builder.putString(WORKER_KEY_CONTENT, content)
            return builder.build()
        }
    }

    override suspend fun doWork(): Result {
        val title = inputData.getString(WORKER_KEY_TITLE)
        val content = inputData.getString(WORKER_KEY_CONTENT)

        if (content != null) {
            try {
                val response = microblogService.createNewPost(title = title, content = content).await()
                if (response.isSuccessful) {
                    if (validResponse(response.body())) {
                        Timber.d("Created post successfully")
                        return Result.success()
                    }
                } else {
                    Timber.d(response.message())

                    val errorResponse = response.errorBody()?.string()
                    if (errorResponse != null) {
                        Timber.d(errorResponse)
                        val moshi = Moshi.Builder().build()
                        val jsonAdapter = moshi.adapter(ErrorResponse::class.java)

                        try {
                            val errorResponseObj = jsonAdapter.fromJson(errorResponse)
                        } catch (e: IOException) {
                        }
                    }
                }
            } catch (throwable: Throwable) {
                Timber.d(throwable)
            }
        } else {
            Timber.e("Received null content")
        }

        return Result.failure()
    }

    private fun validResponse(body: ResponseBody?): Boolean {
        val response = body?.string()
        return if (response != null) {
            Timber.d("Response : $response")
            response.isEmpty()
        } else {
            Timber.d("Response was null")
            false
        }
    }
}