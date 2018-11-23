package com.dialogapp.dialog.db;

import androidx.test.runner.AndroidJUnit4;

import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.repository.Endpoints;
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

import okio.BufferedSource;
import okio.Okio;

import static androidx.test.InstrumentationRegistry.getInstrumentation;
import static com.dialogapp.dialog.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(AndroidJUnit4.class)
public class PostsDaoTest extends DbTest {
    @Test
    public void insertAndLoadTimeline() throws InterruptedException, IOException {
        // arrange
        Moshi moshi = new Moshi.Builder().build();
        Type listMyData = Types.newParameterizedType(List.class, Item.class);
        JsonAdapter<List<Item>> jsonAdapter = moshi.adapter(listMyData);
        final List<Item> timelineItems = jsonAdapter.fromJson(readFromJson("response/timeline.json"));

        // act
        timelineItems.stream().forEach(x -> x.setEndpoint(Endpoints.TIMELINE));
        db.postsDao().insertPosts(timelineItems);

        // assert
        final List<Item> loadedDbDataTimeline = getValue(db.postsDao().loadEndpoint(Endpoints.TIMELINE));
        assertThat(loadedDbDataTimeline.get(0).author.name, is("Dialog2"));
        assertThat(loadedDbDataTimeline.get(10).author.url, is(timelineItems.get(10).author.url));
        assertThat(loadedDbDataTimeline.get(10).id, is(timelineItems.get(10).id));
        assertThat(loadedDbDataTimeline.get(10).microblog.dateRelative, is(timelineItems.get(10).microblog.dateRelative));
    }

    private String readFromJson(String fileName) throws IOException {
        InputStream inputStream = getInstrumentation().getContext().getResources().getAssets().open(fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        return source.readString(StandardCharsets.UTF_8);
    }
}
