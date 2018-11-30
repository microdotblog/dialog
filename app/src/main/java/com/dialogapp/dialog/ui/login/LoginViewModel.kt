package com.dialogapp.dialog.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.auth.PlainTokenAuthenticator
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.util.Event
import com.dialogapp.dialog.vo.Status
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val sessionManager: SessionManager,
                                         private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider,
                                         private val plainTokenAuthenticator: PlainTokenAuthenticator
) : ViewModel() {
    private val parentJob = Job()
    private val scope = CoroutineScope(coroutinesDispatcherProvider.main + parentJob)
    private var loginJob: Job? = null

    private val mutableUiState = MutableLiveData<LoginUiModel>()
    val uiState: LiveData<LoginUiModel>
        get() = mutableUiState

    override fun onCleared() {
        super.onCleared()
        parentJob.cancel()
    }

    fun login(input: String) {
        if (loginJob?.isActive == true)
            return
        loginJob = launchPlainTokenLogin(input)
    }

    private fun launchPlainTokenLogin(inputToken: String): Job {
        return scope.launch(coroutinesDispatcherProvider.computation) {
            withContext(coroutinesDispatcherProvider.main) {
                emitUiState(showProgress = true, enableLoginButton = false)
            }

            val user = plainTokenAuthenticator.verify(inputToken)

            withContext(coroutinesDispatcherProvider.main) {
                if (user.status == Status.SUCCESS) {
                    sessionManager.setLoggedInUser(user.data!!)
                    emitUiState(showSuccess = Event(true))
                } else if (user.status == Status.ERROR) {
                    emitUiState(showProgress = false, showError = Event(user.message!!), enableLoginButton = true)
                }
            }
        }
    }

    private fun emitUiState(
            showProgress: Boolean = false,
            showError: Event<String>? = null,
            showSuccess: Event<Boolean>? = null,
            enableLoginButton: Boolean = false
    ) {
        val uiModel = LoginUiModel(showProgress, showError, showSuccess, enableLoginButton)
        mutableUiState.value = uiModel
    }
}

data class LoginUiModel(
        val showProgress: Boolean,
        val showError: Event<String>?,
        val showSuccess: Event<Boolean>?,
        val enableLoginButton: Boolean
)