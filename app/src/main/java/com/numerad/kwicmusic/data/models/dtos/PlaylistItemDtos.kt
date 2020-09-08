package com.numerad.kwicmusic.data.models.dtos

data class PlaylistItem(
    var kind: String,
    var etag: String,
    var id: String,
    var snippet: SnippetItem,
    var contentDetails: ContentDetailsItem,
    var status: Status
)

data class SnippetItem(
    var publishedAt: String,
    var channelId: String,
    var title: String,
    var description: String,
    var thumbnails: Map<String, Thumbnail> = mapOf(),
    var channelTitle: String,
    var playlistId: String,
    var position: String,
    var resourceId: ResourceId
)

data class ContentDetailsItem(
    var videoId: String,
    var startAt: String,
    var endAt: String,
    var note: String,
    var videoPublishedAt: String
)

data class ResourceId(
    var kind: String,
    var videoId: String
)