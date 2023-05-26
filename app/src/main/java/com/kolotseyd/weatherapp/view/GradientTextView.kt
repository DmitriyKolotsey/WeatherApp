package com.kolotseyd.weatherapp.view

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.util.AttributeSet

class GradientTextView : androidx.appcompat.widget.AppCompatTextView {
    constructor(context: Context) : super(context, null, -1)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs, -1)

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        if (changed) {
            paint.shader = LinearGradient(
                0f, 0f, 0f, height.toFloat(),
                Color.WHITE, Color.GRAY, Shader.TileMode.CLAMP
            )
        }
    }
}