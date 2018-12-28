package com.dialogapp.dialog.ui.posting

import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dialogapp.dialog.workers.NewPostWorker
import com.dialogapp.dialog.workers.ReplyWorker

class PostingViewModel : ViewModel() {

    var title: String? = null
        set(value) {
            field = if (value != null && value.isBlank())
                null
            else
                value
        }
    private var workManager: WorkManager = WorkManager.getInstance()

    fun sendReply(postId: String, content: String) {
        val tag = "REP_$postId"
        val replyRequest = OneTimeWorkRequest.Builder(ReplyWorker::class.java)
                .setInputData(ReplyWorker.createInputData(postId, content))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, replyRequest)
    }

    fun sendPost(content: String) {
        val tag = "POS"
        val newPostRequest = OneTimeWorkRequest.Builder(NewPostWorker::class.java)
                .setInputData(NewPostWorker.createInputData(title, content))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, newPostRequest)
    }
}