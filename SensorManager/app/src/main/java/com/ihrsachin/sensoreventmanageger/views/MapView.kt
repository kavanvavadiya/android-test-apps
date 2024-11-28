package com.ihrsachin.sensoreventmanageger.views

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.util.AttributeSet
import android.util.Log
import com.ihrsachin.sensoreventmanageger.models.MapData

@Suppress("unused")
open class MapView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                 defStyle: Int = 0) : TouchImageView(context, attrs, defStyle) {

    private val tag = this::class.java.simpleName.toString()

    var map :  MapData = MapData()

    var tMatrix = floatArrayOf(0f,0f,0f,0f,0f,0f,0f,0f,0f)

    val beaconPaint : Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
    }
    val cliPaint : Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
    }

    val beaconX = floatArrayOf(17.54f,12.27f,13.99f,16.63f,9.63f, 14.91f, 20.18f,10.95f,19.26f,
        8.32f,21.9f)
    val beaconY = floatArrayOf(14.71f,14.71f,9.59f,9.59f,14.71f,14.71f,14.71f,9.59f,9.59f,9.59f,
        9.59f)
    var cli_x = 15f
    var cli_y = 15f
    var show_cli = false


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        touchMatrix.getValues(tMatrix)
        Log.d(tag, "Canvas Properties : \nHeight = ${height}\n" +
                "Width = ${width}\n" +
                "TouchMatrix : \n" +
                tMatrix.contentToString()
        )
        canvas.save()
        canvas.concat(touchMatrix)
        drawBeacons(canvas)
        if (show_cli){
            drawCli(canvas)
        }
        canvas.restore()
        //get map instance and draw it on canvas
        //map.draw(canvas, touchMatrix)
    }

    private fun drawBeacons(canvas: Canvas){
        var i =0
        while(i<11){
            canvas.drawCircle(beaconX[i]* Resources.getSystem().displayMetrics.density,
                beaconY[i]*Resources.getSystem().displayMetrics.density,
                .2f*Resources.getSystem().displayMetrics.density,beaconPaint)
            i++
        }
    }

    private fun drawCli(canvas: Canvas){
        canvas.drawCircle(cli_x*Resources.getSystem().displayMetrics.density,
        cli_y*Resources.getSystem().displayMetrics.density,
        .2f*Resources.getSystem().displayMetrics.density, cliPaint)
    }
}