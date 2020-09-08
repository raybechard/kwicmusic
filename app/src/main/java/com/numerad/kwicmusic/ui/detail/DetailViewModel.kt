package com.numerad.kwicmusic.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.numerad.kwicmusic.Constants.Companion.API_KEY
import com.numerad.kwicmusic.data.models.dtos.SnippetItem
import com.numerad.kwicmusic.data.models.dtos.VideoListResponse
import com.numerad.kwicmusic.data.models.ui.PlaylistItemUiModel
import com.numerad.kwicmusic.domain.YoutubeService
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber
import java.time.Duration
import kotlin.math.abs

class DetailViewModel : ViewModel(), KoinComponent {

    private lateinit var playlistId: String
    private val disposables = CompositeDisposable()
    private val youtubeService: YoutubeService by inject()

    val titleLiveData: MutableLiveData<String> by lazy { MutableLiveData("") }

    val numberLiveData: MutableLiveData<String> by lazy { MutableLiveData("") }

    val durationLiveData: MutableLiveData<List<String>> by lazy { MutableLiveData(listOf<String>()) }

    val authorsLiveData: MutableLiveData<List<String>> by lazy { MutableLiveData(listOf<String>()) }

    private val itemsLiveData: MutableLiveData<List<PlaylistItemUiModel>> by lazy {
        MutableLiveData<List<PlaylistItemUiModel>>(
            listOf()
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun getItems(): LiveData<List<PlaylistItemUiModel>> {
        updateItems()
        return itemsLiveData
    }

    private fun updateItems() {
        disposables.add(
            youtubeService.getPlaylistItemsSingle(API_KEY, "snippet,contentDetails", playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.items }
                .subscribe(
                    { item ->
                        numberLiveData.value = item.size.toString()
                        itemsLiveData.value = item.map { it.snippet.toItemUiModel("medium") }
                    },
                    Timber::e
                )
        )

        disposables.add(
            youtubeService.getPlaylistItemsSingle(API_KEY, "snippet,contentDetails", playlistId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.items }
                .map { list ->
                    list.map { it.snippet.resourceId.videoId }
                }
                .flatMap { videoIds ->
                    Single.zip(videoIds.map { getDurationSingle(it) }) { it }
                }
                .subscribe(
                    { results ->
                        val responses = results.mapNotNull { it as? VideoListResponse }

                        val durations = responses.map {
                            Duration.parse(it.items[0].contentDetails.duration).toHumanString()
                        }

                        val authors = responses.map {
                            it.items[0].snippet.channelTitle
                        }

                        durationLiveData.value = durations
                        authorsLiveData.value = authors
                    },
                    Timber::e
                )
        )
    }

    private fun Duration.toHumanString(): String {
        val seconds = seconds
        val absSeconds = abs(seconds)
        return String.format(
            "%d:%02d:%02d",
            absSeconds / 3600,
            absSeconds % 3600 / 60,
            absSeconds % 60
        )
    }

    private fun getDurationSingle(videoId: String): Single<VideoListResponse> {
        return youtubeService.getVideoSingle(API_KEY, "snippet,contentDetails", videoId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }

    private fun SnippetItem.toItemUiModel(size: String) = PlaylistItemUiModel(
        title,
        thumbnails[size]?.url ?: "unknown"
    )

    fun setPlaylistId(playlistId: String) {
        this.playlistId = playlistId
    }
}