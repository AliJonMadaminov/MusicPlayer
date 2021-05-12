package com.example.aliplayer.service

import android.app.*
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.provider.MediaStore
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.lifecycle.MutableLiveData
import com.example.aliplayer.R
import com.example.aliplayer.model.Audio
import com.example.aliplayer.receivers.Actions
import com.example.aliplayer.receivers.PlayerNotificationReceiver
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AudioService : Service() {


    private var job: Job? = null
    private val ONGOIG_NOTIFICATION_ID: Int = 45
    private val CHANNEL_ID = "audio_channel_01"

    private var mediaPlayer: MediaPlayer? = null
    var audioList: List<Audio>? = null
    var currentAudio: Audio? = null
    val isAudioCompletedLive: MutableLiveData<Boolean> = MutableLiveData()
    val isNextPressedNotification: MutableLiveData<Boolean> = MutableLiveData()
    val currentPosition: MutableLiveData<Int> = MutableLiveData()

    val isPlayingFromNotification: MutableLiveData<Boolean> = MutableLiveData()
    lateinit var notificationLayout: RemoteViews

    override fun onBind(intent: Intent?): IBinder? {
        return AudioBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        notificationLayout = RemoteViews(packageName, R.layout.notification_layout)
        setNotificationLayoutTexts()

        setUpPendingIntents()

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setOngoing(true)
            .setAutoCancel(true)
            .setCustomContentView(notificationLayout)


        startForeground(ONGOIG_NOTIFICATION_ID, builder.build())

        return START_NOT_STICKY

    }

    override fun onCreate() {
        super.onCreate()

        val receiver = PlayerNotificationReceiver(
            onPauseOrStart = {
                isPlayingFromNotification.value = playAndPostCurrentPositionOrStop()
                setNotificationData()
            },

            onPrev = {
                playPrevious()
                setNotificationData()
                isPlayingFromNotification.value = true
                isNextPressedNotification.value = false
            },

            onNext = {
                playNext()
                setNotificationData()
                isPlayingFromNotification.value = true
                isNextPressedNotification.value = true
            }
        )

        val intentFilter = IntentFilter(Actions.PAUSE_PRESS)
        intentFilter.addAction(Actions.PREVIOUS_PRESSED)
        intentFilter.addAction(Actions.NEXT_PRESS)

        registerReceiver(receiver, intentFilter)

    }

    inner class AudioBinder : Binder() {

        fun getInstance() = this@AudioService

    }


    fun playAudio(audio: Audio) {

        currentAudio = audio

        if (mediaPlayer == null) {

            createMediaPlayer(audio)

        } else {
            resetAudio(audio)
        }
        mediaPlayer?.setOnPreparedListener {
            mediaPlayer?.start()
            setNotificationData()
            postCurrentPosition()
        }

        mediaPlayer?.setOnCompletionListener {
            playNext()
        }


//        mediaPlayer?.setOnCompletionListener {
//            isAudioCompletedLive.value = true
//        }

    }


    private fun createMediaPlayer(audio: Audio) {
        mediaPlayer = MediaPlayer.create(this@AudioService, getContentUri(audio))
    }


    private fun resetAudio(audio: Audio) {
        mediaPlayer?.apply {
            reset()
            getContentUri(audio)?.let { mediaPlayer?.setDataSource(this@AudioService, it) }
            prepareAsync()
        }
    }


    private fun getContentUri(audio: Audio): Uri? {
        return audio.id?.toLong()?.let {
            ContentUris.withAppendedId(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                it
            )
        }
    }

    fun playAndPostCurrentPositionOrStop(): Boolean {
        if (isAudioPlaying()) {
            mediaPlayer?.pause()
        } else {
            mediaPlayer?.start()
            postCurrentPosition()
        }

        setNotificationData()
        return isAudioPlaying()
    }

    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "audio_channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }

    }


    fun playNext() {
        audioList?.let {
            val index = audioList?.indexOf(currentAudio)

            if (index == audioList?.size?.minus(1)) {
                playAudio(it[0])
                currentAudio = it[0]
            } else {

                if (index != null) {
                    playAudio(it[index + 1])
                    currentAudio = it[index + 1]
                }

            }
        }

    }

    fun playPrevious() {
        val index = audioList?.indexOf(currentAudio)

        audioList?.let {

            if (index == 0) {
                playAudio(it[it.size - 1])
            } else {

                if (index != null) {
                    playAudio(it[index - 1])
                    currentAudio = it[index - 1]
                }
            }

        }

    }

    fun isAudioPlaying(): Boolean {
        if (mediaPlayer?.isPlaying != null) {
            return mediaPlayer?.isPlaying!!
        }
        return false
    }

    fun postCurrentPosition() {
        job?.cancel()
        job = GlobalScope.launch {

            while (mediaPlayer?.isPlaying!!
                && mediaPlayer?.currentPosition!! < mediaPlayer?.duration!!
            ) {
                delay(1000)
                currentPosition.postValue(mediaPlayer?.currentPosition)
                Log.d("Something", "postCurrentPosition: ${mediaPlayer?.currentPosition}")
            }
        }
        job?.start()
    }

    fun seekToPostion(progress: Int) {
        mediaPlayer?.seekTo(progress)
    }

    fun setUpPendingIntents() {
        val intent1 = Intent(Actions.PAUSE_PRESS)
        notificationLayout.setOnClickPendingIntent(
            R.id.notification_play_or_pause,
            PendingIntent.getBroadcast(this, 0, intent1, 0)
        )

        val intentOnPrev = Intent(Actions.PREVIOUS_PRESSED)
        notificationLayout.setOnClickPendingIntent(
            R.id.notification_skip_previous,
            PendingIntent.getBroadcast(this, 1, intentOnPrev, 0)
        )

        val intentOnNext = Intent(Actions.NEXT_PRESS)
        notificationLayout.setOnClickPendingIntent(
            R.id.notification_skip_next,
            PendingIntent.getBroadcast(this, 2, intentOnNext, 0)
        )
    }

    fun setNotificationData() {

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setNotificationLayoutTexts()
            setNotificationLayoutIcon()

            val _notification = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setOngoing(true)
                .setCustomContentView(notificationLayout)
                .build()


            notificationManager.notify(ONGOIG_NOTIFICATION_ID, _notification)

        }
    }

    private fun setNotificationLayoutTexts() {
        notificationLayout.setTextViewText(R.id.notification_title, currentAudio?.title)
        notificationLayout.setTextViewText(
            R.id.notification_description,
            currentAudio?.artistName
        )
    }

    private fun setNotificationLayoutIcon() {
        if (isAudioPlaying()) {
            notificationLayout.setImageViewResource(
                R.id.notification_play_or_pause,
                R.drawable.ic_baseline_pause_circle_24
            )
        } else {
            notificationLayout.setImageViewResource(
                R.id.notification_play_or_pause,
                R.drawable.ic_baseline_play_circle_24
            )
        }
    }

    fun setLooping(isLooping:Boolean) {
        mediaPlayer?.isLooping = isLooping
    }
}