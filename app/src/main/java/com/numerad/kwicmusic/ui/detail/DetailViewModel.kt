package com.numerad.kwicmusic.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.Constants.Companion.API_KEY
import com.numerad.kwicmusic.data.model.ContentDetailsItem
import com.numerad.kwicmusic.data.model.ItemUiModel
import com.numerad.kwicmusic.data.model.SnippetItem
import com.numerad.kwicmusic.domain.YoutubeService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class DetailViewModel : ViewModel(), KoinComponent {

    private val disposables = CompositeDisposable()
    private val youtubeService: YoutubeService by inject()

    private val playlistsitemsLiveData: MutableLiveData<List<ItemUiModel>> by lazy {
        MutableLiveData<List<ItemUiModel>>().also {
            listOf<ItemUiModel>()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun getPlaylists(): LiveData<List<ItemUiModel>> {
        updatePlaylists()
        return playlistsitemsLiveData
    }

    private fun updatePlaylists() {
        val playlistId = "PLc18OCfkflEDYWmS7WXiipdMOj-vpP_nX" // test playlist 1

        disposables.add(
            youtubeService.getItemsSingle(API_KEY, "snippet,contentDetails", playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        playlistsitemsLiveData.value =
                            result.items.map { it.snippet.toItemUiModel("medium", it.contentDetails) }
                    },
                    Timber::e
                )
        )
    }

    private fun SnippetItem.toItemUiModel(thumbnailSize: String, contentDetails: ContentDetailsItem?) =
        ItemUiModel(
            title,
            thumbnails[thumbnailSize]?.url ?: "unknown",
            "author", // todo
            11) // contentDetails.endAt.toInt() - contentDetails.startAt.toInt()
}