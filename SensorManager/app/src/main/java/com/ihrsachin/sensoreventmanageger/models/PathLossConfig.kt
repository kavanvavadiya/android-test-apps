package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class PathLossConfig(
    @SerializedName("exponent")val exponent: Float,
    @SerializedName("global_shadow_coeff")val global_shadow_coeff: Float
)
