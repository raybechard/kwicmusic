package com.numerad.kwicmusic.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.models.ui.PlaylistItemUiModel
import com.numerad.kwicmusic.ui.main.MainActivity

class DetailActivity : AppCompatActivity(), DetailFragment.OnItemInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        if (savedInstanceState == null) {
            val title = intent.getStringExtra(MainActivity.ARG_PLAYLIST_TITLE) ?: "unknown"
            val id = intent.getStringExtra(MainActivity.ARG_PLAYLIST_ID) ?: ""
            val thumbnailUrl = intent.getStringExtra(MainActivity.ARG_PLAYLIST_THUMBNAIL) ?: "unknown"

            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance(title, id, thumbnailUrl))
                .commitNow()
        }
    }

    override fun onItemInteraction(playlistItem: PlaylistItemUiModel?) {
    }
}