package com.ihrsachin.sensoreventmanageger.jni_models

interface PositionEventSubscriber {
    fun onPositionChanged(posUpdate : PositionUpdate)
}