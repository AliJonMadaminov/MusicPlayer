package com.example.aliplayer.ui

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aliplayer.R
import com.example.aliplayer.model.Audio
import com.example.aliplayer.viewmodel.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    lateinit var mainViewModel: MainViewModel
    var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = createMainViewModel()

        observeAudioToPlay()
        observeSeekToLive()
        observeShouldStop()
    }

    override fun onDestroy() {
        mediaPlayer?.release()
        super.onDestroy()
    }

    fun observeAudioToPlay() {
        mainViewModel.audioToPlayLive.observe(this, Observer { audio ->
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, getContentUri(audio))
                mediaPlayer?.start()
            } else {
                resetAudio(audio)
            }

            moveSeekBar()

            mediaPlayer?.setOnCompletionListener {
                val audios = mainViewModel.getAudios().value
                val index = audios?.indexOf(audio)
                if (index == audios?.size?.minus(1)) {
                    if (index != null) {
                        mainViewModel.audioToPlayLive.value = audios[0]
                    }
                }
                if (index != null) {
                    mainViewModel.audioToPlayLive.value = audios[index + 1]
                }

            }
        })
    }

    fun observeSeekToLive() {
        mainViewModel.seektoLive.observe(this, Observer {
            mediaPlayer?.seekTo(it)
        })
    }


    fun createMainViewModel(): MainViewModel {
        val mainFactory = MainViewModel.Companion.Factory()
        return ViewModelProvider(this, mainFactory).get(MainViewModel::class.java)
    }


    fun observeShouldStop() {
        mainViewModel.shouldStopLive.observe(this, Observer {
            if (it) {
                mediaPlayer?.pause()
            } else {
                mediaPlayer?.start()
                moveSeekBar()
            }
        })
    }


    fun getContentUri(audio: Audio): Uri? {
        return audio.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }
    }


    fun stopIsNotPressed(): Boolean {
        if (mainViewModel.shouldStopLive.value == null) {
            return true
        }

        return !mainViewModel.shouldStopLive.value!!
    }


    private fun moveSeekBar() {
        job?.cancel()
        job = GlobalScope.launch {
            Log.d("Mythread", "moveSeekBar: ${this.hashCode()}")
            while (mediaPlayer?.isPlaying!!
                && mediaPlayer?.currentPosition!! < mediaPlayer?.duration!!
                && stopIsNotPressed()
            ) {
                delay(1000)
                mainViewModel.audioCurrentPosition.postValue(mediaPlayer?.currentPosition)
            }
        }
        job?.start()
    }

    private fun resetAudio(audio: Audio) {
        mediaPlayer?.apply {
            reset()
            getContentUri(audio)?.let { this.setDataSource(this@MainActivity, it) }
            prepare()
            start()
        }
    }
}