package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object BeaconManager {

    private external fun jni_startScan(max_inactive_flag: Int, active_beacon_flag_update_time: Int)
    private external fun jni_stopScan()

    private external fun jni_saveBeaconConfig(jsonConfig: String)
    private external fun jni_getSavedBeaconConfigs(): String
    private external fun jni_getActiveBeacons(): String

    private external fun jni_resolve(
        uuid: String,
        major: Int,
        minor: Int,
        rssi: Int,
        ref_rssi: Int,
        tx: Int
    )

    //=============================== CLIENT INTERFACE ==============================//
    fun startScan(max_inactive_flag: Int = 4, active_beacon_flag_update_time: Int = 350) {
        jni_startScan(max_inactive_flag, active_beacon_flag_update_time)
    }

    fun stopScan() {
        jni_stopScan()
    }

    fun saveBeaconConfig(config: BeaconConfig) {
        val gson = Gson()
        jni_saveBeaconConfig(gson.toJson(config))
    }

    fun getSavedBeaconConfigs(): ArrayList<BeaconConfig> {
        val gson = Gson()
        val type = object : TypeToken<Array<BeaconConfig>>() {}.type
        return gson.fromJson(jni_getSavedBeaconConfigs(), type)
    }

    fun getActiveBeacons(): ArrayList<ActiveBeacon> {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<ActiveBeacon>>() {}.type
        return gson.fromJson(jni_getActiveBeacons(), type)
    }

    fun resolve(uuid: String,
                major: Int,
                minor: Int,
                rssi: Int,
                ref_rssi: Int,
                tx: Int){
        jni_resolve(uuid, major, minor, rssi, ref_rssi, tx)
    }
}