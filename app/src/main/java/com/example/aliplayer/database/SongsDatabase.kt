package com.example.aliplayer.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aliplayer.model.Audio

@Database(entities = [Audio::class], exportSchema = false, version = 1)
abstract class SongsDatabase : RoomDatabase() {

    val INSTANCE: SongsDatabase? = null

    fun getInstance(context: Context): SongsDatabase {
        var instance = INSTANCE

        if (instance == null) {

            instance = Room.databaseBuilder(
                context.applicationContext,
                SongsDatabase::class.java,
                "songs_database"
            ).fallbackToDestructiveMigration().build()
        }

        return instance
    }
}