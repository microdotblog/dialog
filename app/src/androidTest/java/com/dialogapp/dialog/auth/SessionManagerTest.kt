package com.dialogapp.dialog.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.dialogapp.dialog.api.ServiceInterceptor
import com.dialogapp.dialog.model.LoggedInUser
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock

@RunWith(AndroidJUnit4::class)
class SessionManagerTest {

    private val sharedPreferences: SharedPreferences = InstrumentationRegistry.getInstrumentation()
            .context.getSharedPreferences("test", Context.MODE_PRIVATE)
    private val loggedInUser = LoggedInUser("abc", "fullname", "username",
            "avatar", true, false, "default")

    private lateinit var serviceInterceptor: ServiceInterceptor
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        serviceInterceptor = mock(ServiceInterceptor::class.java)
        sessionManager = SessionManager(sharedPreferences, serviceInterceptor)
    }

    @After
    fun tearDown() {
        // cleanup the shared preferences after every test
        sharedPreferences.edit().clear().commit()
    }

    @Test
    fun userNullOnInit_NoUserInDb() {
        assertNull(sessionManager.user)
    }

    @Test
    fun logout() {
        sessionManager.logout()
        assertFalse(sessionManager.isLoggedIn)
        assertNull(sessionManager.user)
    }

    @Test
    fun isLoggedInGetter_NoUserInDb_False() {
        val isLoggedIn = sessionManager.isLoggedIn
        assertFalse(isLoggedIn)
        assertNull(sessionManager.user)
    }

    @Test
    fun isLoggedInGetter_UserSet_True() {
        sessionManager.setLoggedInUser(loggedInUser)
        val isLoggedIn = sessionManager.isLoggedIn
        assertTrue(isLoggedIn)
        assertEquals(sessionManager.user, loggedInUser)
    }
}