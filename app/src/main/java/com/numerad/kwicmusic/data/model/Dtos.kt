package com.numerad.kwicmusic.data.model

data class Playlist(
    var kind: String,
    var etag: String,
    var id: String,
    var snippet: Snippet,
    var status: Status,
    var contentDetails: ContentDetails,
    var player: Player,
    var localizations: Map<String, Local> = mapOf()
)

data class Snippet(
    var publishedAt: String,
    var channelId: String,
    var title: String,
    var description: String,
    var thumbnails: Map<String, Thumbnail> = mapOf(),
    var channelTitle: String,
    var tags: List<String> = listOf(),
    var defaultLanguage: String,
    var localized: Local
)

data class Status(
    var privacyStatus: String
)

data class ContentDetails(
    var itemCount: Int
)

data class Player(
    var embedHtml: String
)

data class Local(
    var title: String,
    var description: String
)

data class Thumbnail(
    var url: String,
    var widtdh: Int,
    var height: Int
)

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