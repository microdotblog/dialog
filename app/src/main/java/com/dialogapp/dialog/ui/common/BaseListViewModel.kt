package com.dialogapp.dialog.ui.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.dialogapp.dialog.model.Post
import com.dialogapp.dialog.repository.PostsRepository
import com.dialogapp.dialog.util.AbsentLiveData
import com.dialogapp.dialog.vo.CONVERSATION
import com.dialogapp.dialog.vo.DISCOVER
import com.dialogapp.dialog.vo.FAVORITES
import com.dialogapp.dialog.vo.Listing
import com.dialogapp.dialog.vo.Resource
import javax.inject.Inject

class BaseListViewModel @Inject constructor(private val postsRepository: PostsRepository)
    : ViewModel() {

    private val _endpoint = MutableLiveData<EndpointArgs>()
    val endpoint: LiveData<EndpointArgs>
        get() = _endpoint
    val endpointResult: LiveData<Resource<Listing<Post>>> = Transformations
            .switchMap(_endpoint) { endpointArgs ->
                when {
                    endpointArgs.endpoint == DISCOVER ->
                        postsRepository.loadDiscover(endpointArgs.extraArg)
                    endpointArgs.endpoint == FAVORITES ->
                        AbsentLiveData.create()
                    endpointArgs.endpoint == CONVERSATION ->
                        postsRepository.loadConversation(endpointArgs.extraArg ?: "")
                    else -> postsRepository.loadPostsByUsername(endpointArgs.endpoint)
                }
            }

    fun showEndpoint(endpointArgs: EndpointArgs) {
        if (_endpoint.value != endpointArgs) {
            _endpoint.value = endpointArgs
        }
    }

    fun refresh() {
        _endpoint.value?.let {
            _endpoint.value = it
        }
    }
}

data class EndpointArgs(
        val endpoint: String,
        val extraArg: String? = null
)