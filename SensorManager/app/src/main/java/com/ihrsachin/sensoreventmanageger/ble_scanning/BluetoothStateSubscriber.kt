package com.project_ips.piv0.ble_scanning

interface BluetoothStateSubscriber {
    fun onStateTurnedOn()
    fun onStateTurningOn()
    fun onStateTurningOff()
    fun onStateTurnedOf()
}