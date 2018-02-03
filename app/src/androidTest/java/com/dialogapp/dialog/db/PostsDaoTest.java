package com.dialogapp.dialog.db;

import android.support.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.db.entity.Mention;
import com.dialogapp.dialog.db.entity.Timeline;
import com.dialogapp.dialog.model.Author;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.Post;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import okio.BufferedSource;
import okio.Okio;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static com.dialogapp.dialog.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PostsDaoTest extends DbTest {
    @Test
    public void insertAndCheck() throws InterruptedException, IOException {
        // arrange
        Moshi moshi = new Moshi.Builder().build();
        Type listMyData = Types.newParameterizedType(List.class, Item.class);
        JsonAdapter<List<Item>> jsonAdapter = moshi.adapter(listMyData);
        final List<Item> timelineItems = jsonAdapter.fromJson(readFromJson("response/timeline.json"));

        // act
        List<Author> authorsTimeline = timelineItems.stream().map(Item::getAuthor).collect(Collectors.toList());

        db.beginTransaction();
        try {
            db.postsDao().insertAuthors(authorsTimeline);

            timelineItems.forEach(x -> x.post_author_id = db.postsDao().getAuthorIdOf(x.getAuthor().microblog.username));
            db.postsDao().insertPosts(timelineItems);

            List<Timeline> timelineList = timelineItems.stream().map(x -> new Timeline(x.getId(),
                    db.postsDao().getPostIdOf(x.getUrl()))).collect(Collectors.toList());
            db.postsDao().insertTimeline(timelineList);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // assert
        assertThat(db.postsDao().getAuthorsCount(), is(7));
        assertThat(db.postsDao().getPostCount(), is(37));
        assertThat(db.postsDao().getTimelinePostCount(), is(37));

        final List<Post> loadedDbDataTimeline = getValue(db.postsDao().loadTimeline());
        assertThat(loadedDbDataTimeline.get(0).author_name, is("Dialog2"));
        assertThat(loadedDbDataTimeline.get(10).author_avatar_url, is(timelineItems.get(10).getAuthor().avatar));
        assertThat(loadedDbDataTimeline.get(10).id, is(timelineItems.get(10).getId()));
        assertThat(loadedDbDataTimeline.get(10).post_property_dateRelative, is(timelineItems.get(10).getMicroblog().dateRelative));

        // arrange
        final List<Item> mentionsItems = jsonAdapter.fromJson(readFromJson("response/mentions.json"));

        // act
        List<Author> authorsMentions = mentionsItems.stream().map(Item::getAuthor).collect(Collectors.toList());

        db.beginTransaction();
        try {
            db.postsDao().insertAuthors(authorsMentions);

            mentionsItems.forEach(x -> x.post_author_id = db.postsDao().getAuthorIdOf(x.getAuthor().microblog.username));
            db.postsDao().insertPosts(mentionsItems);

            List<Mention> mentionsList = mentionsItems.stream().map(x -> new Mention(x.getId(),
                    db.postsDao().getPostIdOf(x.getUrl()))).collect(Collectors.toList());
            db.postsDao().insertMentions(mentionsList);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        // assert
        assertThat(db.postsDao().getAuthorsCount(), is(7));
        assertThat(db.postsDao().getPostCount(), is(38));
        assertThat(db.postsDao().getMentionsPostCount(), is(2));

        final List<Post> loadedDbDataMentions = getValue(db.postsDao().loadMentions());
        assertThat(loadedDbDataMentions.get(1).author_name, is(mentionsItems.get(1).getAuthor().name));
        assertThat(loadedDbDataMentions.get(1).author_avatar_url, is(mentionsItems.get(1).getAuthor().avatar));
        assertThat(loadedDbDataMentions.get(1).id, is(mentionsItems.get(1).getId()));
        assertThat(loadedDbDataMentions.get(1).post_property_isDeletable, is(mentionsItems.get(1).getMicroblog().isDeletable));
        assertThat(loadedDbDataMentions.get(1).post_property_isFavorite, is(mentionsItems.get(1).getMicroblog().isFavorite));
    }

    private String readFromJson(String fileName) throws IOException {
        InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open(fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        return source.readString(StandardCharsets.UTF_8);
    }
}
