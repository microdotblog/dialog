package com.dialogapp.dialog;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.VerifiedAccount;

import java.util.ArrayList;
import java.util.List;

public class TestUtil {

    public static AccountResponse createAccount(String username) {
        return new AccountResponse("fooBar", "abc@xyz.com",
                username, "xyz.com", "blah blah blah", createRssFeeds(), createPaidSites(),
                true, false);
    }

    private static List<AccountResponse.PaidSite> createPaidSites() {
        List<AccountResponse.PaidSite> paidSites = new ArrayList<>();
        for (int i = 0; i < 3; ++i) {
            AccountResponse.PaidSite p = new AccountResponse.PaidSite((long) i, "guid" + i, "xyz" + i + ".com",
                    "xyz" + i + ".microblog.com", "2017-12-" + i, "summary");
            paidSites.add(p);
        }
        return paidSites;
    }

    private static List<AccountResponse.RssFeed> createRssFeeds() {
        List<AccountResponse.RssFeed> rssFeeds = new ArrayList<>();
        for (int i = 0; i < 2; ++i) {
            AccountResponse.RssFeed r = new AccountResponse.RssFeed((long) i, "xyz" + i + ".com");
            rssFeeds.add(r);
        }
        return rssFeeds;
    }

    public static VerifiedAccount createUnverifiedAccount(boolean invalidAccount) {
        if (invalidAccount)
            return new VerifiedAccount(null, null, null, null,
                    null, null, "api token is invalid");
        else
            return new VerifiedAccount("foo", "foobar", "foo.bar",
                    true, true, "foo.bar", null);
    }
}
