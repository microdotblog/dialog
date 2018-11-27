package com.dialogapp.dialog.auth

import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.db.AccountDao
import com.dialogapp.dialog.model.LoggedInUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(private val accountDao: AccountDao,
                                         private val coroutinesDispatcherProvider: CoroutinesDispatcherProvider) {

    private val scope = CoroutineScope(coroutinesDispatcherProvider.io)

    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        scope.launch {
            user = accountDao.loadAccount()
        }
    }

    fun setLoggedInUser(user: LoggedInUser) {
        scope.launch {
            accountDao.insert(user)
        }
        this.user = user
    }

    fun logout() {
        user = null

        scope.launch {
            accountDao.deleteAccount()
        }
    }
}