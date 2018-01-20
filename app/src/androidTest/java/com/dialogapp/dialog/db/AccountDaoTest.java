package com.dialogapp.dialog.db;

import android.support.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.AccountResponse;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.dialogapp.dialog.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class AccountDaoTest extends DbTest {

    @Test
    public void insertAndLoad() throws Exception {
        final AccountResponse accountResponse = TestUtil.createAccount("foo");
        db.accountDao().insert(accountResponse);

        final AccountResponse loadedAccount = getValue(db.accountDao().fetchAccountInfo("foo"));
        assertThat(loadedAccount.getUsername(), is("foo"));
        assertThat(loadedAccount.getPaidSites().size(), is(3));
        assertThat(loadedAccount.getPaidSites().get(2).getCreatedAt(), is("2017-12-2"));
        assertThat(loadedAccount.getRssFeeds().get(1).getUrl(), is("xyz1.com"));

        final AccountResponse replacementAccount = TestUtil.createAccount("bar");
        db.accountDao().insert(replacementAccount);

        final AccountResponse responseAccount = getValue(db.accountDao().fetchAccountInfo("bar"));
        assertThat(responseAccount.getUsername(), is("bar"));
        assertThat(responseAccount.getRssFeeds().size(), is(2));
        assertThat(responseAccount.getPaidSites().get(2).getCreatedAt(), is("2017-12-2"));
        assertThat(responseAccount.getRssFeeds().get(0).getUrl(), is("xyz0.com"));
    }
}
