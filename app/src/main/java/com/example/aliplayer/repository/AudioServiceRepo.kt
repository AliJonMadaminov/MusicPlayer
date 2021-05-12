package com.example.aliplayer.repository

import android.database.Cursor
import android.provider.MediaStore
import com.example.aliplayer.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AudioServiceRepo {

    var audios: MutableList<Audio> = mutableListOf()

    suspend fun fetchAudios(cursor: Cursor?) {

        withContext(Dispatchers.IO) {

            if (cursor != null && !cursor.isClosed && cursor.moveToFirst()) {

                do {

                    val idIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID)
                    val titleIndex: Int = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
                    val artistIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
                    val durationIndex =
                        cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)
                    val albumId = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM_ID)

                    val id = cursor.getInt(idIndex)
                    val title = cursor.getString(titleIndex)
                    val artist = cursor.getString(artistIndex)
                    val duration = cursor.getInt(durationIndex)
                    val coverPath = cursor.getString(albumId)

                    //TODO get isFavourite from room
                    audios.add(Audio(title, artist, duration, coverPath, false, id))

                } while (cursor.moveToNext())
            }
            cursor?.close()
        }
    }

}