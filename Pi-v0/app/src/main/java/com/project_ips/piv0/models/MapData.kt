package com.project_ips.piv0.models

import android.graphics.Canvas
import android.graphics.Matrix
import android.util.Log

/**
 * Store data relevant to the Floor Map. This includes the base drawable and all the layers
 * present on top of that.
 */
class MapData {
    private val tag = this::class::simpleName.toString()

    private var layers : MutableMap<String, Layer> = mutableMapOf()

    fun addLayer(layer_key : String, layer: Layer){
        layers += layer_key to layer
    }

    fun getLayers(): MutableMap<String, Layer>{
        return layers
    }

    fun draw(canvas: Canvas, touchMatrix: Matrix){
        for ((layer_name, layer) in layers){
            Log.d(tag, "Drawing $layer_name layer")
            layer.draw(canvas, touchMatrix)
        }
    }
}