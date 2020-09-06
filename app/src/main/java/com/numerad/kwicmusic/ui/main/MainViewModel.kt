package com.numerad.numeriq.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.Constants.Companion.API_KEY
import com.numerad.kwicmusic.SessionManager
import com.numerad.kwicmusic.data.model.Snippet
import com.numerad.kwicmusic.data.model.VideoUiModel
import com.numerad.numeriq.domain.VideoService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MainViewModel : ViewModel(), KoinComponent {

    private val disposables = CompositeDisposable()
    private val videoService: VideoService by inject()
    private val sessionManager: SessionManager by inject()

    //    private val videosLiveData: MutableLiveData<List<Video>> by lazy {
    private val videosLiveData: MutableLiveData<List<VideoUiModel>> by lazy {
        MutableLiveData<List<VideoUiModel>>().also {
            listOf<VideoUiModel>()
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun getVideos(): LiveData<List<VideoUiModel>> {
        updateVideos()
        return videosLiveData
    }

    private fun updateVideos() {
        val accessToken = sessionManager.fetchAuthToken()

//        if (accessToken == null) {
//            Timber.e("No access token")
//            return
//        }

        val id = "UChapgGkGWv5Ikx0NKm9Y9CQ" // channel id
//        val id = "hapgGkGWv5Ikx0NKm9Y9CQ" // user id

        disposables.add(
//            videoService.getVideosSingle(accessToken, API_KEY, "snippet,contentDetails", channelId)
            videoService.getVideosSingle(API_KEY, "snippet,contentDetails", id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { result ->
                        videosLiveData.value =
                            result.items.mapNotNull { it.snippet?.toVideoUiModel() }
                    },
                    Timber::e
                )
        )
    }
}

fun Snippet.toVideoUiModel() =
    VideoUiModel(title ?: "unknown", thumbnails?.key?.url ?: "unknown", 1) // todo number