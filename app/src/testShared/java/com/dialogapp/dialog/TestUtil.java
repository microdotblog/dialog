package com.dialogapp.dialog;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.VerifiedAccount;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import okio.BufferedSource;
import okio.Okio;

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

    public static List<Item> readFromJson(ClassLoader classLoader, String fileName) throws IOException {
        Moshi moshi = new Moshi.Builder().build();
        Type listMyData = Types.newParameterizedType(List.class, Item.class);
        JsonAdapter<List<Item>> jsonAdapter = moshi.adapter(listMyData);

        InputStream inputStream = classLoader.getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        return jsonAdapter.fromJson(source.readString(StandardCharsets.UTF_8));
    }
}
