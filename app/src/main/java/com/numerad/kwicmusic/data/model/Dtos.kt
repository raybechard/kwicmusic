package com.numerad.kwicmusic.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaylistItem {
    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("etag")
    @Expose
    var etag: String? = null

    @SerializedName("id")
    @Expose
    var id: String? = null

    @SerializedName("snippet")
    @Expose
    var snippet: Snippet? = null

    @SerializedName("contentDetails")
    @Expose
    var contentDetails: ContentDetails? = null

    @SerializedName("status")
    @Expose
    var status: Status? = null
}

class ContentDetails {
    @SerializedName("videoId")
    @Expose
    var videoId: String? = null

    @SerializedName("startAt")
    @Expose
    var startAt: String? = null

    @SerializedName("endAt")
    @Expose
    var endAt: String? = null

    @SerializedName("note")
    @Expose
    var note: String? = null

    @SerializedName("videoPublishedAt")
    @Expose
    var videoPublishedAt: String? = null
}

class Snippet {
    @SerializedName("publishedAt")
    @Expose
    var publishedAt: String? = null

    @SerializedName("channelId")
    @Expose
    var channelId: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("thumbnails")
    @Expose
    var thumbnails: Thumbnails? = null

    @SerializedName("channelTitle")
    @Expose
    var channelTitle: String? = null

    @SerializedName("playlistId")
    @Expose
    var playlistId: String? = null

    @SerializedName("position")
    @Expose
    var position: String? = null

    @SerializedName("resourceId")
    @Expose
    var resourceId: ResourceId? = null
}

class ResourceId {
    @SerializedName("kind")
    @Expose
    var kind: String? = null

    @SerializedName("videoId")
    @Expose
    var videoId: String? = null
}

class Status {
    @SerializedName("privacyStatus")
    @Expose
    var privacyStatus: String? = null
}

class Thumbnails {
    @SerializedName("key")
    @Expose
    var key: Key? = null
}

class Key {
    @SerializedName("url")
    @Expose
    var url: String? = null

    @SerializedName("width")
    @Expose
    var width: String? = null

    @SerializedName("height")
    @Expose
    var height: String? = null
}
