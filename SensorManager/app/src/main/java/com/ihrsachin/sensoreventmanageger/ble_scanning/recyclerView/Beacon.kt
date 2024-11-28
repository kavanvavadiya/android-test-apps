package com.ihrsachin.sensoreventmanageger.ble_scanning.recyclerView


class Beacon(
    var minor: Int,
    var major: Int,
    var beaconId: String,
    var signStrength: Double,
    var livDist: Double
) {

}