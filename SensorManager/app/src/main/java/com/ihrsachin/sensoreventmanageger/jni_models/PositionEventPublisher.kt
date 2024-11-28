package com.ihrsachin.sensoreventmanageger.jni_models

import android.util.Log
import com.google.gson.Gson

object PositionEventPublisher {
    private val observers = ArrayList<PositionEventSubscriber>()

    private val tag = "PositionEventPublisher"

    private external fun jni_initializePublisher(
        className: String,
        methodName: String,
        signature: String
    )

    init {
        //Pass function meta information to C++
        //And subscribe this publisher as an observer.
        jni_initializePublisher(
            "com/project_ips/piv0/jni_models/PositionEvent", "trigger",
            "(Ljava/lang/String;)V"
        );
    }

    private fun trigger(jsonEventPayload: String) {
        val gson = Gson()
        val posUpdate = gson.fromJson<PositionUpdate>(jsonEventPayload, PositionUpdate::class.java)
        observers.forEach { it.onPositionChanged(posUpdate) }
        Log.d(tag, "Triggered at - (${posUpdate.position.x}, ${posUpdate.position.y}, ${posUpdate.position.z})" +
                " with inaccuracy ${posUpdate.positionInaccuracy}")
    }

    //=============================== CLIENT INTERFACE ==============================//
    fun subscribe(subscriber: PositionEventSubscriber) {
        observers.add(subscriber)
    }

    fun unsubscribe(subscriber: PositionEventSubscriber) {
        observers.remove(subscriber)
    }
}