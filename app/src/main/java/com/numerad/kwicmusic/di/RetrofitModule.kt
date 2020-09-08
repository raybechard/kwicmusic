package com.numerad.kwicmusic.di

import com.google.gson.Gson
import com.numerad.kwicmusic.BuildConfig
import com.numerad.kwicmusic.Constants.Companion.BASE_URL
import com.numerad.kwicmusic.Constants.Companion.HTTP_TIME_OUT
import com.numerad.kwicmusic.domain.YoutubeService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import okhttp3.logging.HttpLoggingInterceptor.Level.NONE
import okhttp3.mockwebserver.MockWebServer
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val retrofitModule = module {
    single { okHttp() }
    single { retrofit(BASE_URL) }
    single { get<Retrofit>().create(YoutubeService::class.java) }
}

val retrofitTestModule = module {
    single { okHttp() }
    single { retrofitTest(get()) }
    single { MockWebServer() }
    factory { get<Retrofit>().create(YoutubeService::class.java) }
}

private fun okHttp() = OkHttpClient().newBuilder()
    .addInterceptor(HttpLoggingInterceptor().apply {
        level =
            if (BuildConfig.DEBUG) BODY else NONE
    })
    .connectTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
    .readTimeout(HTTP_TIME_OUT, TimeUnit.SECONDS)
    .build()

private fun retrofit(baseUrl: String) = Retrofit.Builder()
    .client(okHttp())
    .baseUrl(baseUrl)
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .addConverterFactory(GsonConverterFactory.create(Gson()))
    .build()

private fun retrofitTest(mockWebServer: MockWebServer) = Retrofit.Builder()
    .client(okHttp())
    .baseUrl(mockWebServer.url("/"))
    .addConverterFactory(GsonConverterFactory.create(Gson()))
    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
    .build()
