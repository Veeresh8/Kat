package com.droid.kat

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.hilt.android.testing.HiltAndroidTest
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit

@OptIn(ExperimentalCoroutinesApi::class)
@HiltAndroidTest
class CatDataUseCaseTest {

    private lateinit var mockWebServer: MockWebServer

    private lateinit var katAPIService: KatAPIService
    private lateinit var catDataUseCase: CatDataUseCase

    private val client = OkHttpClient.Builder().build()
    private val contentType = "application/json".toMediaType()

    private val jsonConverter = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
        isLenient = true
    }

    @Before
    fun setup() {
        mockWebServer = MockWebServer()

        katAPIService = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(jsonConverter.asConverterFactory(contentType))
            .build().create(KatAPIService::class.java)

        catDataUseCase = CatDataUseCase(katAPIService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `encounters 429 when cats are loaded`() = runTest {
        val response = MockResponse()
            .setBody("Rate limit reached, please try after some time!")
            .setResponseCode(429)

        mockWebServer.enqueue(response)

        val result = catDataUseCase.fetchCatData(mockk(relaxed = true), mockk(relaxed = true))

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message == "HTTP 429 Client Error")
    }

    @Test
    fun `encounters 400 when cats are loaded`() = runTest {
        val response = MockResponse()
            .setBody("Bad Request Response")
            .setResponseCode(400)

        mockWebServer.enqueue(response)

        val result = catDataUseCase.fetchCatData(mockk(relaxed = true), mockk(relaxed = true))

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message == "HTTP 400 Client Error")
    }

    @Test
    fun `encounters 500 when cats are loaded`() = runTest {
        val response = MockResponse()
            .setBody("Server Gateway Response")
            .setResponseCode(500)

        mockWebServer.enqueue(response)

        val result = catDataUseCase.fetchCatData(mockk(relaxed = true), mockk(relaxed = true))

        assertTrue(result is Result.Error)
        assertTrue((result as Result.Error).exception.message == "HTTP 500 Server Error")
    }

    @Test
    fun `check for malformed JSON in cats`() = runTest {
        val response = MockResponse()
            .setBody("Some Random JSON")
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val result = catDataUseCase.fetchCatData(mockk(relaxed = true), mockk(relaxed = true))
        assertTrue(result is Result.Error)
    }

    @Test
    fun `check successful response for cats`() = runTest {
        val response = MockResponse()
            .setBody(Fake.buildFakeCatsJson())
            .setResponseCode(200)

        mockWebServer.enqueue(response)

        val result =  catDataUseCase.fetchCatData(mockk(relaxed = true), mockk(relaxed = true))
        assertTrue(result is Result.Success)

        assertTrue((result as Result.Success).data.first().id == "Rl39SPjDO")
        assertTrue((result as Result.Success).data.last().url == "https://cdn2.thecatapi.com/images/Rl39SPjDO.png")
    }
}