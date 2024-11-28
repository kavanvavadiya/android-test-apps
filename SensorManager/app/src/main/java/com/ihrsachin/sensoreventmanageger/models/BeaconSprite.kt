package com.ihrsachin.sensoreventmanageger.models

import android.graphics.Canvas
import android.graphics.ColorFilter
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.drawable.VectorDrawable
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat

class BeaconSprite : VectorDrawable(){

    private val redPaint: Paint = Paint().apply { setARGB(255, 0, 0, 255) }

    var imageMatrix : Matrix = Matrix()

    override fun draw(canvas: Canvas) {
        // Get the drawable's bounds
        val width: Int = bounds.width()
        val height: Int = bounds.height()
        val radius: Float = Math.min(width, height).toFloat() / 2f

        // Draw a red circle in the center
        canvas.drawCircle((width / 2).toFloat(), (height / 2).toFloat(), radius, redPaint)
    }

    fun draw(canvas: Canvas, touchMatrix: Matrix){
        imageMatrix = touchMatrix
    }
}