package com.dialogapp.dialog.api

import com.dialogapp.dialog.util.LiveDataCallAdapterFactory
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.IOException

@RunWith(JUnit4::class)
class MicroblogServiceTest {
    private lateinit var microblogService: MicroblogService

    private lateinit var mockWebServer: MockWebServer

    @Before
    @Throws(IOException::class)
    fun setUp() {
        mockWebServer = MockWebServer()
        microblogService = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(MicroblogService::class.java)
    }

    @After
    @Throws(IOException::class)
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    @Throws(IOException::class, InterruptedException::class)
    fun verify() {
        enqueueResponse("verifyresponse.json")
        val response = runBlocking {
            microblogService.verifyToken("abc").await()
        }.body()

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/account/verify"))

        assertThat(response, notNullValue())
        assertThat(response?.token, `is`("abc"))
        assertThat(response?.fullName, `is`("Dialog2"))
        assertThat(response?.gravatarUrl, `is`("avatar.com"))
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(
                mockResponse
                        .setBody(source.readString(Charsets.UTF_8))
        )
    }
}