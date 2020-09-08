package com.numerad.kwicmusic.domain

import com.numerad.kwicmusic.data.models.dtos.ChannelResponse
import com.numerad.kwicmusic.data.models.dtos.PlaylistItemListResponse
import com.numerad.kwicmusic.data.models.dtos.PlaylistListResponse
import com.numerad.kwicmusic.data.models.dtos.VideoListResponse
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
//        @Query("mine") mine: Boolean = true
    ): Single<PlaylistListResponse>

    @GET("youtube/v3/playlistItems")
    fun getPlaylistItemsSingle(
        @Query("key") key: String,
        @Query("part") part: String,
        @Query("playlistId") playlistId: String
    ): Single<PlaylistItemListResponse>

    @GET("youtube/v3/videos")
    fun getVideoSingle(
        @Query("key") key: String,
        @Query("part") part: String,
        @Query("id") id: String
    ): Single<VideoListResponse>

    @GET("youtube/v3/channels")
    fun getChannelSingle(
        @Query("key") key: String,
        @Query("part") part: String,
        @Query("id") id: String
    ): Single<ChannelResponse>

}