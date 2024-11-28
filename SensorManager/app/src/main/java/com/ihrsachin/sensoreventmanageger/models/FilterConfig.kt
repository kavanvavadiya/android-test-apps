package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class FilterConfig constructor(
    @SerializedName("type")val type: String,
    @SerializedName("window_size")val window_size : Int
)
