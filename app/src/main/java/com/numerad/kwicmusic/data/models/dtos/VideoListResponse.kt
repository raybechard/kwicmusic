package com.numerad.kwicmusic.data.models.dtos

data class VideoListResponse(
    var kind: String,
    var etag: String,
    var nextPageToken: String,
    var prevPageToken: String,
    var pageInfo: PageInfo,
    var items: List<Video> = listOf()
)