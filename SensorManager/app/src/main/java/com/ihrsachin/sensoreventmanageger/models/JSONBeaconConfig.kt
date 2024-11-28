package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class JSONBeaconConfig constructor(
    @SerializedName("type")val type : String,
    @SerializedName("kontakt_uid")val kontakt_uid : String,
    @SerializedName("ad_interval")val ad_interval : Int,
    @SerializedName("packet")val packet: String,
    @SerializedName("uuid")val uuid : String,
    @SerializedName("major")val major: Int,
    @SerializedName("minor")val minor : Int,
    @SerializedName("position")val position: Position3,
    @SerializedName("beacon_coeff")val beacon_coeff: Double
)