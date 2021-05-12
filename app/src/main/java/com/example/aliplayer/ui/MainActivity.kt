package com.example.aliplayer.ui

import android.content.*
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.aliplayer.R
import com.example.aliplayer.model.Audio
import com.example.aliplayer.service.AudioService
import com.example.aliplayer.viewmodel.MainViewModel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {


    var audioService:AudioService? = null

    lateinit var mainViewModel: MainViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel = createMainViewModel()
        mainViewModel.fetchAudios(getCursor())
        startAndBindService()
    }

    fun createMainViewModel(): MainViewModel {
        val mainFactory = MainViewModel.Companion.Factory()
        return ViewModelProvider(this, mainFactory).get(MainViewModel::class.java)
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceDisconnected(name: ComponentName?) {

        }

        override fun onServiceConnected(name: ComponentName?, iBinder   : IBinder?) {
            val binder = iBinder as AudioService.AudioBinder
            audioService = binder.getInstance()
        }

    }


    private fun startAndBindService() {

        val intent = Intent(this, AudioService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }else {
            startService(intent)
        }

        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun getCursor(): Cursor? {
        return contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            getProjection(),
            null,
            null,
            MediaStore.Audio.AudioColumns.DATE_ADDED + " DESC"
        )
    }

    private fun getProjection(): Array<String> {

        return arrayOf(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.ARTIST,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.AudioColumns.DURATION,
            MediaStore.Audio.AudioColumns.ALBUM_ID
        )

    }

    override fun onDestroy() {
        super.onDestroy()

    }
}