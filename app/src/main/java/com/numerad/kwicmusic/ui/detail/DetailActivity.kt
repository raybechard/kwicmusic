package com.numerad.kwicmusic.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.numerad.kwicmusic.R
import com.numerad.kwicmusic.data.model.ItemUiModel
import timber.log.Timber

class DetailActivity : AppCompatActivity(), DetailFragment.OnItemInteractionListener {

    private val TAG = "DetailViewModel"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, DetailFragment.newInstance())
                .commitNow()
        }
    }

    override fun onItemInteraction(item: ItemUiModel?) {
        Timber.tag(TAG).e("item thumbnail clicked")
    }
}
