package com.numerad.kwicmusic.ui.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.PlaylistUiModel
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
    }

    override fun onListInteraction(playlist: PlaylistUiModel?) {
        Timber.tag(TAG).e("playlist thumbnail clicked")
    }
}
