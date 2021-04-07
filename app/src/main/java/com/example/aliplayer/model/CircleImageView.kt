package com.example.aliplayer.model

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageView
import com.example.aliplayer.R

class CircleImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    init {
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
        setBackgroundResource(R.drawable.shape_circle)
        scaleType = ScaleType.CENTER_CROP
    }
}