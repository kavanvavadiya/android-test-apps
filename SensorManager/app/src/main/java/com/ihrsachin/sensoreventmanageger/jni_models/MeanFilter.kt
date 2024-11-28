package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName

data class MeanFilter(
    @SerializedName("window") val window: Int,
    @SerializedName("unique_id") override val unique_id: Int,
    @SerializedName("type") override val type: FilterType = FilterType.MEAN_FILTER
) : Filter {}