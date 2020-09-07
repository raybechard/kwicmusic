package com.numerad.kwicmusic.data.model

import com.google.api.client.util.DateTime

class Playlist {
    var kind: String? = null
    var etag: String? = null
    var id: String? = null
    var snippet: Snippet? = null
    var status: Status? = null
    var contentDetails: ContentDetails? = null
    var player: Player? = null
    var localizations: Map<String, Local> = mapOf()
}

class Snippet {
    var publishedAt: String? = null
    var channelId: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnails: Map<String, Thumbnail> = mapOf()
    var channelTitle: String? = null
    var tags: List<String> = listOf()
    var defaultLanguage: String? = null
    var localized: Local? = null
}

class Status {
    var privacyStatus: String? = null
}

class ContentDetails {
    var itemCount: Int? = null
}

class Player {
    var embedHtml: String? = null
}

class Local {
    var title: String? = null
    var description: String? = null
}

class Thumbnail {
    var url: String? = null
    var widtdh: Int? = null
    var height: Int? = null
}

// ************************** PlaylistItem **************************

class PlaylistItem {
    var kind: String? = null
    var etag: String? = null
    var id: String? = null
    var snippet: SnippetItem? = null
    var contentDetails: ContentDetailsItem? = null
    var status: Status? = null
}

class SnippetItem {
    var publishedAt: String? = null
    var channelId: String? = null
    var title: String? = null
    var description: String? = null
    var thumbnails: Map<String, Thumbnail> = mapOf()
    var channelTitle: String? = null
    var playlistId: String? = null
    var position: String? = null
    var resourceId: ResourceId? = null
}

class ContentDetailsItem {
    var videoId: String? = null
    var startAt: String? = null
    var endAt: String? = null
    var note: String? = null
    var videoPublishedAt: DateTime? = null
}

class ResourceId {
    var kind: String? = null
    var videoId: String? = null
}