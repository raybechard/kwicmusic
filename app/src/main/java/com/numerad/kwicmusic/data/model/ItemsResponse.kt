package com.numerad.kwicmusic.data.model

data class PlaylistResponse(
    var kind: String,
    var etag: String,
    var nextPageToken: String,
    var prevPageToken: String,
    var pageInfo: PageInfo,
    var items: List<Playlist> = listOf()
)

data class ItemsResponse(
    var kind: String,
    var etag: String,
    var nextPageToken: String,
    var prevPageToken: String,
    var pageInfo: PageInfo,
    var items: List<PlaylistItem> = listOf()
)

data class PageInfo(
    var totalResults: Int,
    var resultsPerPage: Int
)
