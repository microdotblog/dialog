package com.dialogapp.dialog.api;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.dialogapp.dialog.model.AccountResponse;
import com.dialogapp.dialog.model.Item;
import com.dialogapp.dialog.model.MicroBlogResponse;
import com.dialogapp.dialog.util.LiveDataCallAdapterFactory;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import okio.BufferedSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

import static com.dialogapp.dialog.LiveDataTestUtil.getValue;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class MicroblogServiceTest {
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    private MicroblogService microblogService;

    private MockWebServer mockWebServer;

    @Before
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        microblogService = new Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(new LiveDataCallAdapterFactory())
                .build()
                .create(MicroblogService.class);
    }

    @After
    public void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void getAccount() throws IOException, InterruptedException {
        enqueueResponse("account.json");
        AccountResponse response = getValue(microblogService.getAccountData()).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/account/info"));

        assertThat(response, notNullValue());
        assertThat(response.getEmail(), is("getdialogapp@gmail.com"));
        assertThat(response.getPaidSites().get(0).getGuid(), is("dialog"));
    }

    @Test
    public void getTimeline() throws IOException, InterruptedException {
        enqueueResponse("response.json");
        MicroBlogResponse response = getValue(microblogService.getTimeLine(null)).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/posts/all"));

        List<Item> timelineData = response.items;
        assertThat(timelineData, notNullValue());
        assertThat(timelineData.size(), is(10));
        assertThat(timelineData.get(2).author.microblog.username, is("kanes"));
    }

    @Test
    public void getMentions() throws IOException, InterruptedException {
        enqueueResponse("response.json");
        MicroBlogResponse response = getValue(microblogService.getMentions(null)).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/posts/mentions"));

        List<Item> mentionsData = response.items;
        assertThat(mentionsData, notNullValue());
        assertThat(mentionsData.size(), is(10));
        assertThat(mentionsData.get(2).author.microblog.username, is("kanes"));
    }

    @Test
    public void getFavorites() throws IOException, InterruptedException {
        enqueueResponse("response.json");
        MicroBlogResponse response = getValue(microblogService.getFavorites()).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/posts/favorites"));

        List<Item> favoritesData = response.items;
        assertThat(favoritesData, notNullValue());
        assertThat(favoritesData.size(), is(10));
        assertThat(favoritesData.get(2).author.microblog.username, is("kanes"));
    }

    @Test
    public void getUserPosts() throws IOException, InterruptedException {
        enqueueResponse("userpostsresponse.json");
        MicroBlogResponse response = getValue(microblogService.getPostsByUsername("dialog")).body;

        RecordedRequest request = mockWebServer.takeRequest();
        assertThat(request.getPath(), is("/posts/dialog"));

        List<Item> favoritesData = response.items;
        assertThat(favoritesData, notNullValue());
        assertThat(response.microblog, notNullValue());
        assertThat(response.author, notNullValue());
        assertThat(response.microblog.following_count, is(190));
        assertThat(response.author.name, is("Dialog"));
        assertThat(favoritesData.get(0).author.name, is("Dialog2"));
    }

    private void enqueueResponse(String fileName) throws IOException {
        enqueueResponse(fileName, Collections.<String, String>emptyMap());
    }

    private void enqueueResponse(String fileName, Map<String, String> headers) throws IOException {
        InputStream inputStream = getClass().getClassLoader()
                .getResourceAsStream("api-response/" + fileName);
        BufferedSource source = Okio.buffer(Okio.source(inputStream));
        MockResponse mockResponse = new MockResponse();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            mockResponse.addHeader(header.getKey(), header.getValue());
        }
        mockWebServer.enqueue(mockResponse
                .setBody(source.readString(StandardCharsets.UTF_8)));
    }
}