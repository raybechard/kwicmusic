package com.numerad.numeriq.di

import com.squareup.picasso.Picasso
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val picassoModule = module {
    single {
        Picasso.with(androidContext())
    }
}

//private fun okHttp3Downloader(client: OkHttpClient) = OkHttp3Downloader(client)
//
//private fun picasso(context: Context, downloader: OkHttp3Downloader) =
//    Picasso.Builder(context)
//        .downloader(downloader)
