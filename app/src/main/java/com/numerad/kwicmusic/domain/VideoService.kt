package com.numerad.numeriq.domain

import com.numerad.kwicmusic.data.model.YoutubeResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

//https://developers.google.com/youtube/v3/guides/implementation/playlists
//https://developers.google.com/apis-explorer/#p/youtube/v3/youtube.playlists.list?part=snippet,contentDetails&mine=true
interface VideoService {
    @GET("youtube/v3/playlists")
    fun getVideosSingle(
//        @Header("Authorization") authHeader: String,
        @Query("key") key: String,
        @Query("part") part: String,
//        @Query("id") id: String
        @Query("channelId") channelId: String
//        @Query("mine") mine: Boolean = false
    ): Single<YoutubeResponse>
}