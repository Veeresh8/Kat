package com.droid.kat

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideContentType() = "application/json".toMediaType()

    @Provides
    @Singleton
    fun providesHttpClient(
        networkInterceptor: HttpLoggingInterceptor,
        authInterceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addNetworkInterceptor(networkInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun providesNetworkInterceptor(): HttpLoggingInterceptor {
        val logLevel = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }

        return HttpLoggingInterceptor()
            .setLevel(level = logLevel)
    }

    @Provides
    @Singleton
    fun providesHeaders(): Interceptor {
        return Interceptor { chain ->
            val request =
                chain.request().newBuilder()
                    .header("x-api-key", "live_aXV0fAEuHcSHZq4PKWBvwj0Oinwlu2FMAZP2GQoL7wzv75X7yPStD24Xz59dbb5s")
                    .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun providesJsonSerializer(): Json {
        return Json {
            ignoreUnknownKeys = true
            prettyPrint = true
            isLenient = true
            explicitNulls = false
        }
    }

    @Singleton
    fun providesRetrofit(okHttpClient: OkHttpClient, json: Json, contentType: MediaType): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .baseUrl("https://api.thecatapi.com/")
            .build()
    }

    @Provides
    @Singleton
    fun providesKatApiService(retrofit: Retrofit): KatAPIService {
        return retrofit.create(KatAPIService::class.java)
    }
}


