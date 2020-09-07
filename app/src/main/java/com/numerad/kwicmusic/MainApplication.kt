package com.numerad.kwicmusic

import android.app.Application
import com.numerad.kwicmusic.di.mainModule
import com.numerad.kwicmusic.di.picassoModule
import com.numerad.kwicmusic.di.retrofitModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(applicationContext)
            modules(listOf(mainModule, retrofitModule, picassoModule))
        }
    }
}