package com.numerad.kwicmusic.data.models.ui

data class PlaylistItemUiModel(
    val title: String,
    val thumbnailUrl: String,
    val author: String = "",
    val duration: String = ""
)