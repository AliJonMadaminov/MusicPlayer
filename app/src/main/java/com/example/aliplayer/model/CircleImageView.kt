package com.alimadaminov.fragmentssecond

import android.content.Context
import android.util.AttributeSet
import android.view.ViewOutlineProvider
import androidx.appcompat.widget.AppCompatImageView

class CircleImageView(context: Context, attrs: AttributeSet?) : AppCompatImageView(context, attrs) {
    init {
        outlineProvider = ViewOutlineProvider.BACKGROUND
        clipToOutline = true
        setBackgroundResource(R.drawable.circle_shape)
        scaleType = ScaleType.CENTER_CROP
    } //    public CircleImageView(@NonNull Context context) {
    //        super(context);
    //        setOutlineProvider(ViewOutlineProvider.BACKGROUND);
    //        setClipToOutline(true);
    //        setBackgroundResource(R.drawable.circle_shape);
    //        setScaleType(ScaleType.CENTER_CROP);
    //    }
}