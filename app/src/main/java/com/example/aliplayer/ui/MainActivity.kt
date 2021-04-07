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
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aliplayer.R
import com.example.aliplayer.model.Audio
import com.example.aliplayer.viewmodel.MainViewModel

class MainActivity : AppCompatActivity() {

    var mediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mainViewModel = createMainViewModel()
        mainViewModel.audioToPlayLive.observe(this, Observer { audio ->
            if (mediaPlayer == null) {
                mediaPlayer = MediaPlayer.create(this, getContentUri(audio))
            } else {
                mediaPlayer!!.reset()
                getContentUri(audio)?.let { mediaPlayer!!.setDataSource(this, it) }
                mediaPlayer!!.prepare()
            }
            mediaPlayer?.start()
        })
    }


    fun createMainViewModel(): MainViewModel {
        val mainFactory = MainViewModel.Companion.Factory()
        return ViewModelProvider(this, mainFactory).get(MainViewModel::class.java)
    }

    fun getContentUri(audio: Audio): Uri? {
        return audio.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }
    }
}