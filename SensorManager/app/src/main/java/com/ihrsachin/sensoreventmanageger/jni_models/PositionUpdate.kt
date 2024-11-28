package com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName
import com.ihrsachin.sensoreventmanageger.models.Position3

data class PositionUpdate(
    @SerializedName("position")val position: Position3,
    @SerializedName("position_inaccuracy")val positionInaccuracy:Double
)
