package com.example.aliplayer.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.aliplayer.model.Audio

@Dao
interface FavouriteAudiosDao {

    @Insert
    fun insert(audio: Audio)

    @Update
    fun update(audio: Audio)

    @Query("SELECT * FROM favourite_audios")
    fun getAll():Array<Audio>
}