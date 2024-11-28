package  com.ihrsachin.sensoreventmanageger.jni_models

import com.google.gson.annotations.SerializedName
import  com.ihrsachin.sensoreventmanageger.models.Position3
import com.ihrsachin.sensoreventmanageger.jni_models.KalmanFilter

data class BeaconConfig(
    @SerializedName("uuid") val uuid: String,
    @SerializedName("major") val major: Int,
    @SerializedName("minor") val minor: Int
) {
    @SerializedName("position")var position = Position3(0.0, 0.0, -100.0)
    @SerializedName("beacon_coeff")var beacon_coeff: Double = 1.0
    @SerializedName("x_sigma")var x_sigma: Double = 0.0

    @SerializedName("rssi_filters")val filters: ArrayList<Filter> = ArrayList()

    fun addFilter(filter: KalmanFilter) {
        filters.add(filter)
    }

    fun removeFilter(unique_id: Int) {
        filters.removeAll { it.getID() == unique_id }
    }
}