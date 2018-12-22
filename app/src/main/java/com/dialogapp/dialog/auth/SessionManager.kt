package com.dialogapp.dialog.auth

import android.content.SharedPreferences
import androidx.core.content.edit
import com.dialogapp.dialog.CoroutinesDispatcherProvider
import com.dialogapp.dialog.api.ServiceInterceptor
import com.dialogapp.dialog.db.MicroBlogDb
import com.dialogapp.dialog.model.LoggedInUser
import com.dialogapp.dialog.repository.PostsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(private val prefs: SharedPreferences,
                                         private val db: MicroBlogDb,
                                         private val postsRepository: PostsRepository,
                                         private val serviceInterceptor: ServiceInterceptor,
                                         dispatchers: CoroutinesDispatcherProvider) {

    var user: LoggedInUser? = null
        private set(value) {
            field = value
            serviceInterceptor.authToken = value?.token ?: ""
        }

    val isLoggedIn: Boolean
        get() = user != null

    private val scope = CoroutineScope(dispatchers.io)

    init {
        val token = prefs.getString(KEY_USER_TOKEN, null)
        if (token != null) {
            user = LoggedInUser(token, prefs.getString(KEY_USER_FULLNAME, "")!!,
                    prefs.getString(KEY_USER_USERNAME, "")!!, prefs.getString(KEY_USER_AVATAR, "")!!,
                    prefs.getBoolean(KEY_USER_HASSITE, false), prefs.getBoolean(KEY_USER_FULLACCESS, false),
                    prefs.getString(KEY_USER_DEFAULTSITE, "")!!)
        }
    }

    fun setLoggedInUser(user: LoggedInUser) {
        this.user = user
        prefs.edit {
            putString(KEY_USER_TOKEN, user.token)
            putString(KEY_USER_FULLNAME, user.fullName)
            putString(KEY_USER_USERNAME, user.username)
            putString(KEY_USER_AVATAR, user.gravatarUrl)
            putBoolean(KEY_USER_HASSITE, user.hasSite)
            putBoolean(KEY_USER_FULLACCESS, user.isFullaccess)
            putString(KEY_USER_DEFAULTSITE, user.defaultSite)
        }
    }

    fun logout() {
        prefs.edit().clear().apply()
        scope.launch {
            db.runInTransaction {
                db.posts().clear()
                db.endpointData().clear()
            }
            postsRepository.clearFavorites()
        }
        user = null
    }

    companion object {
        private const val KEY_USER_TOKEN = "KEY_USER_TOKEN"
        private const val KEY_USER_FULLNAME = "KEY_USER_FULLNAME"
        private const val KEY_USER_USERNAME = "KEY_USER_USERNAME"
        private const val KEY_USER_AVATAR = "KEY_USER_AVATAR"
        private const val KEY_USER_HASSITE = "KEY_USER_HASSITE"
        private const val KEY_USER_FULLACCESS = "KEY_USER_FULLACCESS"
        private const val KEY_USER_DEFAULTSITE = "KEY_USER_DEFAULTSITE"
    }
}