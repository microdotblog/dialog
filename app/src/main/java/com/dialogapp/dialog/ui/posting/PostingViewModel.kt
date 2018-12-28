package com.dialogapp.dialog.ui.posting

import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.dialogapp.dialog.workers.ReplyWorker

class PostingViewModel : ViewModel() {

    private var workManager: WorkManager = WorkManager.getInstance()

    fun sendReply(postId: String, content: String) {
        val tag = "REP_$postId"
        val replyRequest = OneTimeWorkRequest.Builder(ReplyWorker::class.java)
                .setInputData(ReplyWorker.createInputData(postId, content))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, replyRequest)
    }
}