package com.example.aliplayer.repository

import android.database.Cursor
import android.provider.MediaStore
import androidx.lifecycle.MutableLiveData
import com.example.aliplayer.model.Audio
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainRepo {

    var audioLiveData: MutableLiveData<List<Audio>> = MutableLiveData()

    suspend fun fetchAudios(cursor: Cursor?) {

        val audios: MutableList<Audio> = mutableListOf()

        withContext(Dispatchers.IO) {

            if (cursor != null && !cursor.isClosed && cursor.moveToFirst()) {

                do {
                    if (cursor.isAfterLast || cursor.isClosed) {
                        break;
                    }

                    val idIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns._ID)
                    val titleIndex: Int = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)
                    val artistIndex = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)
                    val durationIndex =
                        cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DURATION)

                    val id = cursor.getInt(idIndex)
                    val title = cursor.getString(titleIndex)
                    val artist = cursor.getString(artistIndex)
                    val duration = cursor.getInt(durationIndex)

                    audios.add(Audio(title, artist, duration, id))

                } while (cursor.moveToNext())
            }
            cursor?.close()
            audioLiveData.postValue(audios)
        }

    }

}