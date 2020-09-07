package com.numerad.kwicmusic.domain

import com.numerad.kwicmusic.data.model.ItemsResponse
import com.numerad.kwicmusic.data.model.PlaylistResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface YoutubeService {

    @GET("youtube/v3/playlists")
    fun getPlaylistsSingle(
//        @Header("Authorization") authHeader: String,
        @Query("key") key: String,
        @Query("part") part: String,
        @Query("id") id: String  // playlist id
//        @Query("channelId") channelId: String
//        @Query("mine") mine: Boolean = false
    ): Single<PlaylistResponse>

    @GET("youtube/v3/playlistItems")
    fun getItemsSingle(
        @Query("key") key: String,
        @Query("part") part: String,
//        @Query("id") id: String,
        @Query("playlistId") playlistId: String
    ): Single<ItemsResponse>

}