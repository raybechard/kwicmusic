package com.numerad.kwicmusic.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.Constants.Companion.API_KEY
import com.numerad.kwicmusic.SessionManager
import com.numerad.kwicmusic.data.model.PlaylistUiModel
import com.numerad.kwicmusic.data.model.Snippet
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

    //    private val videosLiveData: MutableLiveData<List<Video>> by lazy {
    private val playlistsLiveData: MutableLiveData<List<PlaylistUiModel>> by lazy {
        MutableLiveData<List<PlaylistUiModel>>().also {
            listOf<PlaylistUiModel>()
        }
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
        val accessToken = sessionManager.fetchAuthToken()

//        if (accessToken == null) {
//            Timber.e("No access token")
//            return
//        }

        val channelId = "UChapgGkGWv5Ikx0NKm9Y9CQ" // channel id

        // playlist ids
        val playlistId = "PLc18OCfkflEDYWmS7WXiipdMOj-vpP_nX" // test playlist 1
//        val playlistId = "PLc18OCfkflECeXw6rlkyvd5wurQuWfDcz" // test playlist 2
//        val playlistId = "PLc18OCfkflEB1U1hrWE4ns0IYQV8KeLd0" // test playlist 3
//        val playlistId = "PLc18OCfkflEAY4LWBVwPCaMeNXstHAkOy" // channel playlist

        disposables.add(
//            videoService.getVideosSingle(accessToken, API_KEY, "snippet,contentDetails", channelId)
            youtubeService.getPlaylistsSingle(API_KEY, "snippet,contentDetails", playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        playlistsLiveData.value =
                            result.items.map {
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

    private fun Snippet.toPlaylistUiModel(playlistId: String, thumbnailSize: String, itemCount: Int) =
        PlaylistUiModel(
            playlistId,
            title,
            thumbnails[thumbnailSize]?.url ?: "unknown",
            itemCount
        )
}