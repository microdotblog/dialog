package com.dialogapp.dialog.db

import androidx.test.runner.AndroidJUnit4
import com.dialogapp.dialog.model.LoggedInUser
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AccountDaoTest : DbTest() {

    @Test
    fun insertAndRead() {
        val account = LoggedInUser("abc", "fullname", "username",
                "avatar", true, false, "default")
        db.accountDao().insert(account)
        val loaded = db.accountDao().loadAccount()
        MatcherAssert.assertThat(loaded, CoreMatchers.notNullValue())
        MatcherAssert.assertThat(loaded?.token, CoreMatchers.`is`("abc"))
        MatcherAssert.assertThat(loaded?.fullName, CoreMatchers.`is`("fullname"))
        MatcherAssert.assertThat(loaded?.username, CoreMatchers.notNullValue())
    }
}