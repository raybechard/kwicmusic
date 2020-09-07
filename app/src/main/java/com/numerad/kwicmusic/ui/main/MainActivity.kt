package com.numerad.kwicmusic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.PlaylistUiModel
import com.numerad.kwicmusic.ui.detail.DetailActivity
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

    override fun onListInteraction(playlist: PlaylistUiModel) {
        Timber.tag(TAG).e("playlist thumbnail clicked")

        val bundle = Bundle().apply {
            putString(ARG_PLAYLIST_ID, playlist.id)
        }
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
//        finish()
    }

    companion object {
        const val ARG_PLAYLIST_ID = "ARG_PLAYLIST_ID"
    }
}
