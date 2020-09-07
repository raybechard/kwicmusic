package com.numerad.kwicmusic.di

import com.google.gson.GsonBuilder
import com.numerad.kwicmusic.SessionManager
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val mainModule = module {
    single { GsonBuilder().setLenient().create() }
    single { SessionManager(androidContext()) }
}
