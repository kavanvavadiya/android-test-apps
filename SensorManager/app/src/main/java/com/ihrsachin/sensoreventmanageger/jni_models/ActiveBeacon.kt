package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName

data class ActiveBeacon (@SerializedName("config")val config : BeaconConfig) {
    @SerializedName("raw_rssi")var raw_rssi = 0
    @SerializedName("rssi")var rssi = 0.0
    @SerializedName("ref_rssi")var ref_rssi = -100
    @SerializedName("tx_power")var tx_Power = -100
    @SerializedName("inactive_flag")var inactive_flag = 0
}
