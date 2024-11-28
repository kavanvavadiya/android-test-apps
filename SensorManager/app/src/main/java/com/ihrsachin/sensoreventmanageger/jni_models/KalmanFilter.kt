package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName

data class KalmanFilter(
    @SerializedName("initial_state")val initial_rssi_estimate : Int,
    @SerializedName("initial_covariance")val initial_rssi_error : Double,
    @SerializedName("measurement_noise")val measurement_noise : Double,
    @SerializedName("process_noise")val process_noise : Double,
    @SerializedName("unique_id")override val unique_id: Int,
    @SerializedName("type")override val type: FilterType = FilterType.KALMAN_FILTER

) : Filter {}