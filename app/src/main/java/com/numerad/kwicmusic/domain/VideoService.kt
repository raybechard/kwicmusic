package com.numerad.numeriq.domain

import com.numerad.kwicmusic.data.model.YoutubeResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface VideoService {
    @GET("youtube/v3/playlists")
    fun getVideosSingle(
        @Query("key") key: String,
        @Query("mine") mine: Boolean = true
    ): Single<YoutubeResponse>
}