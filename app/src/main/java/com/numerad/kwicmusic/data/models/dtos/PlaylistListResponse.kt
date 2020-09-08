package com.numerad.kwicmusic.data.models.dtos

data class PlaylistListResponse(
    var kind: String,
    var etag: String,
    var nextPageToken: String,
    var prevPageToken: String,
    var pageInfo: PageInfo,
    var items: List<Playlist> = listOf()
)