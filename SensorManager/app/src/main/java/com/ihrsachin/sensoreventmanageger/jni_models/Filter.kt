package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName

enum class FilterType{
    @SerializedName("mean_filter")MEAN_FILTER,
    @SerializedName("kalman_filter")KALMAN_FILTER
}

interface Filter{
    val unique_id: Int
    val type : FilterType
    public fun getID() = unique_id
}
