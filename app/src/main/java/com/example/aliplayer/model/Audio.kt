package com.example.aliplayer.model

data class Audio(
    var title: String? = null,
    var artistName: String? = null,
    var duration: Int,
    val id: Int? = null
)