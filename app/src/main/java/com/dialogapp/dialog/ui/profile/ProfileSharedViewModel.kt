package com.dialogapp.dialog.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dialogapp.dialog.model.EndpointData

class ProfileSharedViewModel : ViewModel() {
    private val endpointData = MutableLiveData<EndpointData>()
    var currentProfile: String? = null

    fun setEndpointData(newEndpointData: EndpointData?) {
        if (endpointData.value != newEndpointData) {
            endpointData.value = newEndpointData
        }
    }

    fun getEndpointData(): LiveData<EndpointData> {
        return endpointData
    }

    fun isProfileCurrentlyShown(username: String): Boolean {
        return username.equals(currentProfile, ignoreCase = true)
    }
}