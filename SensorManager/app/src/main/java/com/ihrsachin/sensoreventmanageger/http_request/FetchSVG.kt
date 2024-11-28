package com.ihrsachin.sensoreventmanageger.http_request


import android.content.Context
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.Log
import android.widget.ImageView
import com.pixplicity.sharp.Sharp
import com.ihrsachin.sensoreventmanageger.R
import okhttp3.*
import java.io.IOException
import java.io.InputStream
import java.util.concurrent.TimeUnit


class FetchSVG {
    private lateinit var httpClient: OkHttpClient
    // this method is used to fetch svg and load it into
    // target imageview.
    fun fetchSvg(
        context: Context, url: String?,
        target: ImageView
    ){
        httpClient = OkHttpClient.Builder()
            .cache(
                Cache(
                    context.cacheDir,
                    5 * 1024 * 1014
                )
            )
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build()

        // here we are making HTTP call to fetch data from
        // URL.
        try {
            val request = Request.Builder().url(url!!).build()
            httpClient.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    // we are adding a default image if we gets
                    // any error.
                    target.setImageResource(
                        R.drawable.h18_floor_9
                    )
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    // sharp is a library which will load stream
                    // which we generated from url in our target
                    // imageview.
                    val stream: InputStream = response.body!!.byteStream()
                    val drawable = Sharp.loadInputStream(stream).drawable

                    // calculating factor to change unit from pixel to dp
                    val metrics = context.resources.displayMetrics
                    val alpha: Float = metrics.density

                    val resizedDrawable = getResizedDrawable(drawable, alpha)
                    Log.d("alpha", alpha.toString())
                    target.setImageDrawable(resizedDrawable as Drawable)

                    stream.close()
                }
            })
        } catch (e : Exception){
            target.setImageResource(
                R.drawable.h18_floor_9
            )
        }


    }

    fun getResizedDrawable(drawable: Drawable, scale: Float) =
        LayerDrawable(arrayOf(drawable)).also { it.setLayerSize(0, (drawable.intrinsicWidth * scale).toInt(), (drawable.intrinsicHeight * scale).toInt()) }
}