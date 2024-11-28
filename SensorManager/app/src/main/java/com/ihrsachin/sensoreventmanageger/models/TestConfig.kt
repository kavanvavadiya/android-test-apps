package com.ihrsachin.sensoreventmanageger.models

import com.google.gson.annotations.SerializedName

data class TestConfig constructor(
    @SerializedName("place_of_recording")val place_of_recording : String,
    @SerializedName("type_of_test")val type_of_test : String,
    @SerializedName("beacons")val JSONBeaconConfigs : ArrayList<JSONBeaconConfig>,
    @SerializedName("filters")val filterConfigs : ArrayList<FilterConfig>,
    @SerializedName("path_loss")val pathLossConfig: PathLossConfig,
    @SerializedName("markers")val markerPositions : ArrayList<Position2>
)