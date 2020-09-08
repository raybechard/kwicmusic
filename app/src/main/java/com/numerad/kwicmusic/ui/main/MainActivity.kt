package com.numerad.kwicmusic.ui.main

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.models.ui.PlaylistUiModel
import com.numerad.kwicmusic.ui.detail.DetailActivity
import com.numerad.kwicmusic.ui.login.LoginActivity

class MainActivity : AppCompatActivity(), MainFragment.OnListInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userName = intent.getStringExtra(LoginActivity.ARG_USER_NAME) ?: ""

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(userName))
                .commitNow()
        }
    }

    override fun onListInteraction(playlist: PlaylistUiModel) {
        val bundle = Bundle().apply {
            putString(ARG_PLAYLIST_ID, playlist.id)
            putString(ARG_PLAYLIST_TITLE, playlist.title)
            putString(ARG_PLAYLIST_THUMBNAIL, playlist.thumbnailUrl)
        }
        val intent = Intent(this, DetailActivity::class.java)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    companion object {
        const val ARG_PLAYLIST_ID = "ARG_PLAYLIST_ID"
        const val ARG_PLAYLIST_TITLE = "ARG_PLAYLIST_TITLE"
        const val ARG_PLAYLIST_THUMBNAIL = "ARG_PLAYLIST_THUMBNAIL"
    }
}
