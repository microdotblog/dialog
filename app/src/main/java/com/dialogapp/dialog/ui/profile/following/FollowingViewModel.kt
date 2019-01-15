package com.dialogapp.dialog.ui.profile.following

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.dialogapp.dialog.model.FollowingAccount
import com.dialogapp.dialog.repository.PostsRepository
import com.dialogapp.dialog.vo.Resource
import javax.inject.Inject

class FollowingViewModel @Inject constructor(private val postsRepository: PostsRepository)
    : ViewModel() {

    private val _endpoint = MutableLiveData<FollowingEndpointArgs>()
    val endpoint: LiveData<FollowingEndpointArgs>
        get() = _endpoint
    val endpointResult: LiveData<Resource<List<FollowingAccount>>> = Transformations
            .switchMap(_endpoint) { endpointArgs ->
                postsRepository.loadFollowing(endpointArgs.username,
                        endpointArgs.isSelf, endpointArgs.refresh)
            }

    fun showEndpoint(endpointArgs: FollowingEndpointArgs) {
        if (_endpoint.value != endpointArgs) {
            _endpoint.value = endpointArgs
        }
    }

    fun refresh() {
        _endpoint.value = _endpoint.value?.let {
            it.refresh = true
            it
        }
    }
}

data class FollowingEndpointArgs(
        val username: String,
        val isSelf: Boolean = false,
        var refresh: Boolean = false
)