package com.numerad.kwicmusic.data.model

data class VideoUiModel(
    val title: String,
    val thumbnailUrl: String,
    val number: Int
)

data class Video(
    var source: String,
    var author: String,
    var title: String,
    var description: String,
    var url: String,
    var urlToImage: String,
    var publishedAt: String,
    var content: String
)