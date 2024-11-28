package com.ihrsachin.sensoreventmanageger.models

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.Log

class Layer {

    private val tag = this::class::simpleName.toString()

    /**
     * Layers should be shown by default.
     */
    var is_show : Boolean = true

    /**
     * Every Layer would have certain Drawables
     */
    private var sprites : MutableList<Drawable> = mutableListOf()

    var scaleMode : ScaleModeSprite = ScaleModeSprite.NONE

    var layerType : LayerType = LayerType.NONE

    /**
     * Layers with higher [z_value] should be rendered on top. If two layers share the same
     * z_value the layer which is accessed later(possibly in a list) would be drawn on top.
     */
    var z_value : Int = 0

    fun addSprite(sprite : Drawable){
        sprites.add(sprite)
    }

    fun draw(canvas: Canvas, touchMatrix: Matrix){
        for (sprite in  sprites){
            Log.d(tag, "Drawing  sprite ${sprite.toString()}")
            sprite.draw(canvas)
        }
    }
}