package com.ihrsachin.sensoreventmanageger.jni_models

object TrilaterationManager{
    private external fun jni_startTrilateration(updateFrequency : Int, maxCyclesInQueue : Int)
    private external fun jni_stopTrilateration()

    fun startTrilateration(updateFrequency : Int = 350, maxCyclesInQueue : Int = 5){
        jni_startTrilateration(updateFrequency, maxCyclesInQueue)
    }

    fun stopTrilateration(){
        jni_stopTrilateration()
    }
}