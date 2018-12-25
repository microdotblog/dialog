package com.dialogapp.dialog.ui.login

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.LiveDataTestUtil
import com.dialogapp.dialog.auth.PlainTokenAuthenticator
import com.dialogapp.dialog.auth.SessionManager
import com.dialogapp.dialog.loggedInUser
import com.dialogapp.dialog.util.Event
import com.dialogapp.dialog.vo.Resource
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

class LoginViewModelTest {

    @get:Rule
    var instantTaskExecutor = InstantTaskExecutorRule()

    private val sessionManager: SessionManager = mock()
    private val plainTokenAuthenticator: PlainTokenAuthenticator = mock()
    private val coroutinesDispatcherProvider =
            CoroutinesDispatcherProvider(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined)

    private val viewModel = LoginViewModel(sessionManager, coroutinesDispatcherProvider, plainTokenAuthenticator)

    @Test
    fun login_userLoggedInSuccessfully() = runBlocking {
        whenever(plainTokenAuthenticator.verify("abc")).thenReturn(Resource.success(loggedInUser))

        viewModel.login("abc")

        val expected = LoginUiModel(
                showProgress = false,
                showError = null,
                showSuccess = Event(true),
                enableLoginButton = false
        )
        val uiState = LiveDataTestUtil.getValue(viewModel.uiState)
        assertEquals(uiState, expected)
    }

    @Test
    fun login_userLogInFailed() = runBlocking {
        whenever(plainTokenAuthenticator.verify("abc")).thenReturn(Resource.error("error", null))

        viewModel.login("abc")

        val expected = LoginUiModel(
                showProgress = false,
                showError = Event("error"),
                showSuccess = null,
                enableLoginButton = true
        )
        val uiState = LiveDataTestUtil.getValue(viewModel.uiState)
        assertEquals(uiState, expected)
    }
}