package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class Position2(
    @SerializedName("x") var x: Double,
    @SerializedName("y") var y: Double
)