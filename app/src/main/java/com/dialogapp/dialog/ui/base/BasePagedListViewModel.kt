package com.dialogapp.dialog.ui.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import androidx.lifecycle.Transformations.switchMap
import androidx.lifecycle.ViewModel
import com.dialogapp.dialog.repository.PostsPagingRepository
import javax.inject.Inject

class BasePagedListViewModel @Inject constructor(private val postsPagingRepository: PostsPagingRepository)
    : ViewModel() {

    private val endpoint = MutableLiveData<String>()
    private val endpointResult = map(endpoint) {
        postsPagingRepository.postsOfEndpoint(endpoint = it)
    }

    val posts = switchMap(endpointResult) { it.pagedList }!!
    val endpointData = switchMap(endpointResult) { it.endpointData }
    val networkState = switchMap(endpointResult) { it.networkState }
    val refreshState = switchMap(endpointResult) { it.refreshState }

    fun refresh() {
        endpointResult.value?.refresh?.invoke()
    }

    fun showEndpoint(endpoint: String): Boolean {
        if (this.endpoint.value == endpoint)
            return false
        this.endpoint.value = endpoint
        return true
    }

    fun retry() {
        val listing = endpointResult.value
        listing?.retry?.invoke()
    }

    fun currentEndpoint(): String? = endpoint.value
}