package com.dialogapp.dialog.auth

import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.api.ServiceInterceptor
import com.dialogapp.dialog.db.AccountDao
import com.dialogapp.dialog.loggedInUser
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import org.junit.Assert.*
import org.junit.Test

class SessionManagerTest {

    private val accountDao: AccountDao = mock()
    private val serviceInterceptor: ServiceInterceptor = mock()
    private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider = CoroutinesDispatcherProvider(
            Dispatchers.Unconfined,
            Dispatchers.Unconfined,
            Dispatchers.Unconfined
    )
    private val sessionManager = SessionManager(accountDao, serviceInterceptor, coroutinesDispatcherProvider)

    @Test
    fun accountNull_ByDefault() {
        assertNull(sessionManager.user)
    }

    @Test
    fun setLoggedInUser() {
        sessionManager.setLoggedInUser(loggedInUser)
        verify(accountDao).insert(loggedInUser)
        assertEquals(sessionManager.user, loggedInUser)
    }

    @Test
    fun logout() {
        sessionManager.logout()
        verify(accountDao).deleteAccount()
        assertNull(sessionManager.user)
    }

    @Test
    fun isLoggedInGetter_NoUserInDb_False() {
        whenever(accountDao.loadAccount()).thenReturn(null)
        val isLoggedIn = sessionManager.isLoggedIn
        assertFalse(isLoggedIn)
        verify(accountDao).loadAccount()
        assertNull(sessionManager.user)
    }

    @Test
    fun isLoggedInGetter_UserInDb_TrueAndDbCalled() {
        whenever(accountDao.loadAccount()).thenReturn(loggedInUser)
        val isLoggedIn = sessionManager.isLoggedIn
        assertTrue(isLoggedIn)
        verify(accountDao).loadAccount()
        assertEquals(sessionManager.user, loggedInUser)
    }

    @Test
    fun isLoggedInGetter_UserInCache_TrueAndDbNotCalled() {
        sessionManager.setLoggedInUser(loggedInUser)
        val isLoggedIn = sessionManager.isLoggedIn
        assertTrue(isLoggedIn)
        assertEquals(sessionManager.user, loggedInUser)
        verify(accountDao, never()).loadAccount()
    }
}