package com.dialogapp.dialog.db;

import android.support.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.TestUtil;
import com.dialogapp.dialog.model.Item;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static com.dialogapp.dialog.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PostsDaoTest extends DbTest {
    @Test
    public void insertAndLoadTimeline() throws InterruptedException {
        final List<Item> timelineItems = TestUtil.createListOfEndpoint("timeline");
        db.postsDao().insertTimeline(timelineItems);

        final List<Item> loadedTimelineData = getValue(db.postsDao().loadEndpoint("timeline"));
        assertThat(loadedTimelineData.get(8).getEndpoint(), is("timeline"));
        assertThat(loadedTimelineData.get(3).getId(), is(3L));
        assertThat(loadedTimelineData.get(7).getAuthor().getName(), is("foobar7"));
        assertThat(loadedTimelineData.get(7).getAuthor().getMicroblog().getUsername(), is("foo7"));
    }

    @Test
    public void insertAndLoadMentions() throws InterruptedException {
        final List<Item> mentionsItems = TestUtil.createListOfEndpoint("mentions");
        db.postsDao().insertMentions(mentionsItems);

        final List<Item> loadedMentionsData = getValue(db.postsDao().loadEndpoint("mentions"));
        assertThat(loadedMentionsData.get(5).getEndpoint(), is("mentions"));
        assertThat(loadedMentionsData.get(21).getContentHtml(), is("blahblahblah21"));
        assertThat(loadedMentionsData.get(28).getAuthor().getUrl(), is("www.foo28.com"));
        assertThat(loadedMentionsData.get(27).getMicroblog().getIsDeletable(), is(false));
    }
}
