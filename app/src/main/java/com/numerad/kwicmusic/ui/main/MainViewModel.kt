package com.numerad.kwicmusic.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.Constants.Companion.API_KEY
import com.numerad.kwicmusic.SessionManager
import com.numerad.kwicmusic.data.models.dtos.Snippet
import com.numerad.kwicmusic.data.models.ui.PlaylistUiModel
import com.numerad.kwicmusic.domain.YoutubeService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MainViewModel : ViewModel(), KoinComponent {

    private val disposables = CompositeDisposable()
    private val youtubeService: YoutubeService by inject()
    private val sessionManager: SessionManager by inject()

    private val playlistsLiveData: MutableLiveData<List<PlaylistUiModel>> by lazy {
        MutableLiveData<List<PlaylistUiModel>>(listOf())
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun getPlaylists(): LiveData<List<PlaylistUiModel>> {
        updatePlaylists()
        return playlistsLiveData
    }

    private fun updatePlaylists() {
//        val accessToken = sessionManager.fetchAuthToken()
//        if (accessToken == null) {
//            Timber.e("No access token")
//            return
//        }

        val playlistId = "PLc18OCfkflEDYWmS7WXiipdMOj-vpP_nX" // test playlist 1

        disposables.add(
//            youtubeService.getPlaylistsSingle(accessToken, API_KEY, "snippet,contentDetails")
            youtubeService.getPlaylistsSingle(API_KEY, "snippet,contentDetails", playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.items }
                .subscribe(
                    { playlistList ->
                        playlistsLiveData.value =
                            playlistList.map {
                                it.snippet.toPlaylistUiModel(
                                    it.id,
                                    "medium",
                                    it.contentDetails.itemCount
                                )
                            }
                    },
                    Timber::e
                )
        )
    }

    private fun Snippet.toPlaylistUiModel(playlistId: String, size: String, itemCount: Int) =
        PlaylistUiModel(
            playlistId,
            title,
            thumbnails[size]?.url ?: "unknown",
            itemCount
        )
}