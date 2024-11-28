package com.ihrsachin.sensoreventmanageger.http_request

import android.util.Log
import okhttp3.*
import okhttp3.WebSocket
import okio.ByteString
import java.util.concurrent.TimeUnit



class WebSocket {
    private val TAG = "WebSocket"
    private val token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ0b2tlbl90eXBlIjoicmVmcmVzaCIsImV4cCI6MTY4NTM2MDQ1MSwiaWF0IjoxNjg1Mjc0MDUxLCJqdGkiOiJjYWRhMWI3YmFkMmQ0YmM5YTYwNjBmMjJmN2FhY2MxNiIsInVzZXJfaWQiOjF9.de5u0BZoQ2UvyXOw9GayQG8t5RR9NK-lIlJPvKCcQUc"
    private val wsUrl = "ws://ec2-65-1-93-114.ap-south-1.compute.amazonaws.com/ws/ajwsc/location_updates/1/?token=${token}"
    private lateinit var ws : WebSocket
    fun connect(){
        val client = OkHttpClient().newBuilder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
            .build();
        val request = Request.Builder()
            .url(wsUrl)
            .build()
        ws = client.newWebSocket(request, object : WebSocketListener() {

            // Override methods to handle WebSocket events
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "websocket is closed with reason: $reason")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "code: $code, websocket is being closed with reason: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, "websocket failed with response: $response")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d(TAG, "msg: $text")
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, "msg: $bytes")
            }
            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "websocket is opened with response: $response")
            }
        })
    }

    fun sendMsg(msg: String){
        ws.send(msg)
    }
}