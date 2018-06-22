package com.dialogapp.dialog.ui.profilescreen;

public class UserInfo {
    public final String microblog_bio;
    public final String author_author_name;
    public final String author_author_url;
    public final String author_author_avatar_url;
    public final boolean microblog_is_following;
    public final boolean microblog_is_you;
    public final int microblog_following_count;

    public UserInfo(String microblog_bio, String author_author_name,
                    String author_author_url, String author_author_avatar_url,
                    boolean microblog_is_following, boolean microblog_is_you, int microblog_following_count) {
        this.microblog_bio = microblog_bio;
        this.author_author_name = author_author_name;
        this.author_author_url = author_author_url;
        this.author_author_avatar_url = author_author_avatar_url;
        this.microblog_is_following = microblog_is_following;
        this.microblog_is_you = microblog_is_you;
        this.microblog_following_count = microblog_following_count;
    }
}
