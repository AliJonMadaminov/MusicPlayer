package com.example.aliplayer.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class PlayerNotificationReceiver(
    val onPauseOrStart: () -> Unit,
    val onPrev: () -> Unit,
    val onNext: () -> Unit
) :
    BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("ActionOnRec", "onReceive: ${intent?.action}")
        when (intent?.action) {

            Actions.PAUSE_PRESS -> onPauseOrStart()

            Actions.PREVIOUS_PRESSED -> onPrev()

            Actions.NEXT_PRESS -> onNext()
        }
    }

    constructor() : this({}, {}, {})

//    companion object {
//        fun getInstance(_onPauseOrStart: () -> Unit):PlayerNotificationReceiver {
//
//            return PlayerNotificationReceiver()
//        }
//    }
}


object Actions {
    val PAUSE_PRESS = "PAUSE PRESS ACTION"
    val NEXT_PRESS = "NEXT PRESS ACTION"
    val PREVIOUS_PRESSED = "PREV PRESS ACTION"
}