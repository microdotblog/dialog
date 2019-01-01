package com.dialogapp.dialog.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.dialogapp.dialog.workers.*

class RequestViewModel : ViewModel() {

    var title: String? = null
        set(value) {
            field = if (value != null && value.isBlank())
                null
            else
                value
        }

    private val _replyTag = MutableLiveData<String>()
    private val _newPostTag = MutableLiveData<String>()
    private val _deleteTag = MutableLiveData<String>()

    private var workManager: WorkManager = WorkManager.getInstance()
    private var replyWorkInfo: LiveData<List<WorkInfo>>? = Transformations
            .switchMap(_replyTag) { tag ->
                workManager.getWorkInfosByTagLiveData(tag)
            }
    private var newPostWorkInfo: LiveData<List<WorkInfo>>? = Transformations
            .switchMap(_newPostTag) { tag ->
                workManager.getWorkInfosByTagLiveData(tag)
            }
    private var deleteWorkInfo: LiveData<List<WorkInfo>>? = Transformations
            .switchMap(_deleteTag) { tag ->
                workManager.getWorkInfosByTagLiveData(tag)
            }

    fun sendFavoriteRequest(postId: String?, belongsToEndpoint: String?) {
        val tag = "FAV_$postId"
        val favoriteRequest = OneTimeWorkRequest.Builder(FavoriteWorker::class.java)
                .setInputData(FavoriteWorker.createInputData(postId, belongsToEndpoint))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, favoriteRequest)
    }

    fun sendDeleteRequest(postId: String) {
        val tag = "DEL_$postId"
        val deletePostRequest = OneTimeWorkRequest.Builder(DeletePostWorker::class.java)
                .setInputData(DeletePostWorker.createInputData(postId))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, deletePostRequest)
    }

    fun sendFollowRequest(username: String, isFollowing: Boolean) {
        val tag = "FOL_$username"
        val followRequest = OneTimeWorkRequest.Builder(FollowWorker::class.java)
                .setInputData(FollowWorker.createInputData(username, isFollowing))
                .addTag(tag)
                .build()
        WorkManager.getInstance().enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, followRequest)
    }

    fun sendReply(postId: String, content: String) {
        val tag = "REP_$postId"
        val replyRequest = OneTimeWorkRequest.Builder(ReplyWorker::class.java)
                .setInputData(ReplyWorker.createInputData(postId, content))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, replyRequest)
        _replyTag.value = tag
    }

    fun sendPost(content: String) {
        val tag = "NEW"
        val newPostRequest = OneTimeWorkRequest.Builder(NewPostWorker::class.java)
                .setInputData(NewPostWorker.createInputData(title, content))
                .addTag(tag)
                .build()
        workManager.enqueueUniqueWork(tag, ExistingWorkPolicy.KEEP, newPostRequest)
        _newPostTag.value = tag
    }

    fun getReplyWorkInfo(): LiveData<List<WorkInfo>>? {
        return replyWorkInfo
    }

    fun getNewPostWorkInfo(): LiveData<List<WorkInfo>>? {
        return newPostWorkInfo
    }

    fun getDeleteWorkInfo(): LiveData<List<WorkInfo>>? {
        return deleteWorkInfo
    }
}