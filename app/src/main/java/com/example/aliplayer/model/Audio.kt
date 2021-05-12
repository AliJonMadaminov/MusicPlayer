package com.example.aliplayer.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_audios")
data class Audio(
    var title: String? = null,
    var artistName: String? = null,
    var duration: Int,
    var coverPath:String? = null,
    var isFavourite:Boolean = false,

    @PrimaryKey(autoGenerate = true)
    val id: Int? = null
)