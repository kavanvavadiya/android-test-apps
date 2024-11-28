package com.ihrsachin.sensoreventmanageger.jni_models

import android.util.Log
import com.google.gson.Gson

interface BeaconEventSubscriber {
    fun onBeaconAdded(beacon : ActiveBeacon)
    fun onBeaconRemoved(beacon: ActiveBeacon)
    fun onBeaconUpdated(beacon: ActiveBeacon)
}

object BeaconEventPublisher {
    private val observers = ArrayList<BeaconEventSubscriber>()
    private val tag = "BeaconEvent"

    private external fun jni_initializePublisher(className : String, methodName : String, signature : String)

    init{
        //Pass function meta information to C++
        //And subscribe this publisher as an observer.
        jni_initializePublisher("com/project_ips/piv0/jni_models/BeaconEvent","trigger",
            "(ILjava/lang/String;)V");
    }

    private fun trigger(beaconEventType : Int, jsonEventPayload : String) {
        val gson = Gson();
        Log.d(tag, jsonEventPayload);
        val beacon = gson.fromJson<ActiveBeacon>(jsonEventPayload, ActiveBeacon::class.java)
        if (beaconEventType == 0){
            //Add event
            observers.forEach { it.onBeaconAdded(beacon) }
        }
        else if(beaconEventType == 1){
            //Update Event
            observers.forEach { it.onBeaconUpdated(beacon) }
        }
        else if (beaconEventType == 2){
            observers.forEach { it.onBeaconRemoved(beacon) }
        }
        else{
            Log.e(tag, "Undefined Beacon Event triggered from JNI")
        }
    }

    //=============================== CLIENT INTERFACE ==============================//

    fun subscribe(subscriber: BeaconEventSubscriber){
        observers.add(subscriber)
    }

    fun unsubscribe(subscriber: BeaconEventSubscriber){
        observers.remove(subscriber)
    }
}