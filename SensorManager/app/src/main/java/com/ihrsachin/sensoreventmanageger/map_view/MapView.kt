package com.ihrsachin.sensoreventmanageger.map_view

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Path
import android.util.AttributeSet
import android.util.Log
import com.ihrsachin.sensoreventmanageger.models.Position2

@Suppress("unused")
open class MapView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null,
    defStyle: Int = 0
) : TouchImageView(context, attrs, defStyle) {

    private val tag = this::class.java.simpleName.toString()

    private val beaconPaint: Paint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.GREEN
    }

    private var beaconPositions = ArrayList<Position2>()

    var markerIndex: Int = -1

    private val density = Resources.getSystem().displayMetrics.density

    private val linePaths = ArrayList<Path>()
    private val circlePaths = ArrayList<Path>()

    private val uncoveredPathPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.DKGRAY
        strokeWidth = .1F * density
        style = Paint.Style.FILL_AND_STROKE
    }

    private val coveredPathPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        strokeWidth = .1F * density
        style = Paint.Style.FILL_AND_STROKE
    }

    private val cliStrokePaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLACK
        strokeWidth = .1F * density
        style = Paint.Style.STROKE
    }

    private val cliFillPaint = Paint(ANTI_ALIAS_FLAG).apply {
        color = Color.BLUE
        style = Paint.Style.FILL
    }

    var showCli = false
    var cliPosition = Position2(0.0, 0.0)

    var firstEstimateSet = false
//    set(value) {
//        value.x = (0.1F*value.x + field.x)/1.1F
//        value.y = (0.1F*value.y + field.y)/1.1F
//        field = value
//    }

    /**
     * Store the x,y coordinates respectively.
     */
    var markerPositions = ArrayList<Position2>()

    private val markerPath = Path()

    init {
    }

    fun initialiseMarkerPositions(positions: ArrayList<Position2>) {
        markerPositions = positions
        createDynamicDrawableObjects()
    }

    fun initializeBeaconPositions(positions: ArrayList<Position2>) {
        beaconPositions = positions
        createDynamicDrawableObjects()
    }

    fun onPositionUpdate(x: Double, y: Double) {
        if (!firstEstimateSet) {
            firstEstimateSet = true
        }
        showCli = true
        cliPosition = Position2(x, y)
        invalidate()
    }

    /**
     * This function is responsible for creating all the dynamic drawable objects such as paths and paints.
     */
    private fun createDynamicDrawableObjects() {
        //TODO:
        val it = markerPositions.listIterator()
        while (it.hasNext()) {
            val tempCircle = Path()
            val tempLine = Path()
            val currentPosition = it.next()
            //Add a circle at current position
            tempCircle.moveTo(
                (currentPosition.x * density).toFloat(),
                (currentPosition.y * density).toFloat()
            )
            tempCircle.addCircle(
                (currentPosition.x * density).toFloat(),
                (currentPosition.y * density).toFloat(),
                0.2f * density,
                Path.Direction.CW
            )
            circlePaths.add(tempCircle)

            //Add a line
            tempLine.moveTo(
                (currentPosition.x * density).toFloat(),
                (currentPosition.y * density).toFloat()
            )
            if (it.hasNext()) {
                val nextPosition = it.next()
                tempLine.lineTo(
                    (nextPosition.x * density).toFloat(),
                    (nextPosition.y * density).toFloat()
                )
                it.previous()
            } else {
                //We're ate Last marker position
                tempLine.lineTo(
                    (markerPositions.first().x * density).toFloat(),
                    (markerPositions.first().y * density).toFloat()
                )
            }
            linePaths.add(tempLine)
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.save()
        canvas.concat(touchMatrix)
        drawBeacons(canvas)
        drawPath(canvas)
        if (showCli) {
            drawCli(canvas)
        }
        canvas.restore()
    }

    private fun drawPath(canvas: Canvas) {
        Log.d(tag, markerIndex.toString())
        Log.d(tag, markerPositions.toString())
        //TODO: draw Path
        var index = 0
        for (line in linePaths) {
            if (index <= markerIndex) {
                canvas.drawPath(line, coveredPathPaint)
            } else {
                canvas.drawPath(line, uncoveredPathPaint)
            }
            index++
        }

        index = 0
        for (circle in circlePaths) {
            if (index <= markerIndex) {
                canvas.drawPath(circle, coveredPathPaint)
            } else {
                canvas.drawPath(circle, uncoveredPathPaint)
            }
            index++
        }
    }

    private fun drawBeacons(canvas: Canvas) {
//        Log.d(tag, "1")
        for (beaconPosition in beaconPositions) {
            canvas.drawCircle(
                (beaconPosition.x * density).toFloat(),
                (beaconPosition.y * density).toFloat(),
                .2f * density,
                beaconPaint
            )
//            Log.d(tag,"2")
        }
    }

    private fun drawCli(canvas: Canvas) {
        Log.d(tag, "Drawing CLI at ${cliPosition.x},${cliPosition.y}")
        canvas.drawCircle(
            (cliPosition.x * density).toFloat(),
            (cliPosition.y * density).toFloat(),
            1.2f * density, cliStrokePaint
        )
        canvas.drawCircle(
            (cliPosition.x * density).toFloat(),
            (cliPosition.y * density).toFloat(),
            1.2f * density, cliFillPaint
        )
    }
}