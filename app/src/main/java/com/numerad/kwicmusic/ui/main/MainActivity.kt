package com.numerad.kwicmusic.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.Video
import com.numerad.numeriq.di.mainModule
import com.numerad.numeriq.di.picassoModule
import com.numerad.numeriq.di.retrofitModule
import com.numerad.numeriq.ui.main.MainFragment
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import timber.log.Timber


class MainActivity : AppCompatActivity(), MainFragment.OnListInteractionListener {

    private val TAG = "MainViewModel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commitNow()
        }

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidContext(this@MainActivity)
            modules(listOf(mainModule, retrofitModule, picassoModule))
        }
    }

    override fun onListInteraction(article: Video?) {
        Timber.tag(TAG).e("list clicked")
    }
}
