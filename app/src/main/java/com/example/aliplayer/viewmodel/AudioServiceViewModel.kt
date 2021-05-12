package com.example.aliplayer.viewmodel

import android.database.Cursor
import androidx.lifecycle.MutableLiveData
import com.example.aliplayer.model.Audio
import com.example.aliplayer.repository.AudioServiceRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AudioServiceViewModel {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)
    private val audioServiceRepo = AudioServiceRepo()


    fun getAudios(): MutableList<Audio> {
        return audioServiceRepo.audios
    }

    fun fetchAudios(cursor: Cursor?) {
        viewModelScope.launch {
            audioServiceRepo.fetchAudios(cursor)
        }
    }


//    companion object {
//
//        class Factory : ViewModelProvider.Factory {
//            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
//                if (modelClass == AudioServiceViewModel::class.java) {
//                    return AudioServiceViewModel as T
//                }
//
//                throw IllegalArgumentException("This factory takes only AudioServiceViewModel class")
//            }
//
//        }
//    }

}