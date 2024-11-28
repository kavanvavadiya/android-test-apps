package com.ihrsachin.sensoreventmanageger.models

import com.ihrsachin.sensoreventmanageger.jni_models.BeaconConfig

class Map {
    val id: Int
    val name: String
    val floorPlan: String
    val location: String
    val beaconCount: Int
    val longitude: String
    val latitude: String
    val scaleFactor: Float
    val rotation: Float
    var testConfig : TestConfig?

    constructor(id: Int, name: String, floorPlan: String) {
        this.id = id
        this.name = name
        this.floorPlan = floorPlan
        location = ""
        beaconCount = 0
        latitude = ""
        longitude = ""
        scaleFactor = 1F
        rotation = 0F
        testConfig = null
    }

    constructor(
        id: Int,
        name: String,
        floorPlan: String,
        location: String,
        beaconCount: Int,
        longitude: String,
        latitude: String,
        scaleFactor: Float,
        rotation: Float,
        testConfig: TestConfig
    ) {
        this.id = id
        this.name = name
        this.floorPlan = floorPlan
        this.location = location
        this.beaconCount = beaconCount
        this.longitude = longitude
        this.latitude = latitude
        this.scaleFactor = scaleFactor
        this.rotation = rotation
        this.testConfig = testConfig
    }

    override fun toString(): String {
        return this.name
    }
}