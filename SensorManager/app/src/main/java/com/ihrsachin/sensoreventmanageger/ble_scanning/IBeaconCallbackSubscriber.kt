package com.ihrsachin.sensoreventmanageger.ble_scanning

interface IBeaconCallbackSubscriber {
    fun onIBeaconCallbackReceived(uuid : String, major : Int, minor : Int, rssi : Int, rssi_1m :
    Int, txPower : Int)
}