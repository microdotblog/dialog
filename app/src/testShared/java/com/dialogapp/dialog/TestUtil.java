package com.dialogapp.dialog;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Author;
import com.dialogapp.dialog.model.Item;
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

    public static List<Item> createListOfEndpoint(String endpoint) {
        List<Item> items = new ArrayList<>();
        for (int i = 0; i < 30; ++i) {
            Item item = new Item();
            item.setEndpoint(endpoint);
            item.setId((long) i);
            item.setUrl("url" + i);
            item.setDatePublished("2018-1-" + i);
            item.setContentHtml("blahblahblah" + i);
            item.setAuthor(createAuthor(i));
            item.setMicroblog(createItemProperty(i));

            items.add(item);
        }

        return items;
    }

    private static Author createAuthor(int i) {
        Author author = new Author();
        author.setName("foobar" + i);
        author.setUrl("www.foo" + i + ".com");
        author.setAvatar("avatar.jpg");
        author.setMicroblog(createAuthorInfo(i));
        return author;
    }

    private static Author.Microblog_ createAuthorInfo(int i) {
        return new Author.Microblog_("foo" + i);
    }

    private static Item.Microblog__ createItemProperty(int i) {
        Item.Microblog__ property;
        if (i % 2 == 0)
            property = new Item.Microblog__(true, true, "2018-1-" + i);
        else
            property = new Item.Microblog__(false, false, "2018-2-" + i);
        return property;
    }
}
