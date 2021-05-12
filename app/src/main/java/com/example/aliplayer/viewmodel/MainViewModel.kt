package com.example.aliplayer.viewmodel

import android.database.Cursor
import androidx.lifecycle.*
import com.example.aliplayer.model.Audio
import com.example.aliplayer.repository.AudioServiceRepo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class MainViewModel private constructor() : ViewModel() {

    private val job = Job()
    private val viewModelScope = CoroutineScope(Dispatchers.Main + job)
    private val mainRepo = AudioServiceRepo()


    val audioToPlayLive: MutableLiveData<Audio> = MutableLiveData()
    val shouldStopLive: MutableLiveData<Boolean> = MutableLiveData()
    val seektoLive: MutableLiveData<Int> = MutableLiveData()
    val audioCurrentPosition = MutableLiveData<Int>()


    fun fetchAudios(cursor: Cursor?) {
        viewModelScope.launch {
            mainRepo.fetchAudios(cursor)
        }
    }

    fun getAudios() = mainRepo.audios

    companion object {

        class Factory : ViewModelProvider.Factory {
            override fun <T : ViewModel?> create(modelClass: Class<T>): T {
                if (modelClass == MainViewModel::class.java) {
                    return MainViewModel() as T
                }

                throw IllegalArgumentException("This factory takes only MainViewModel class")
            }

        }
    }

}