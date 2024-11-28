package com.ihrsachin.sensoreventmanageger.ble_scanning

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.ihrsachin.sensoreventmanageger.ble_scanning.IBeaconCallbackSubscriber
import com.ihrsachin.sensoreventmanageger.ble_scanning.ApplicationBLEManager

/**
 * This fragment allows you to turn bluetooth on/off.
 */

abstract class BleScanFragment : Fragment(), IBeaconCallbackSubscriber {
    private val applicationBleManager : ApplicationBLEManager by activityViewModels()

    fun startScanning(){
        this.activity?.let { applicationBleManager.startBleScan(it) }
    }

    fun stopScanning(){
        applicationBleManager.stopBleScan()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        applicationBleManager.registerIBeaconCallbackSubscriber(this)
    }

    fun getScanState() : Boolean{
        if(applicationBleManager.scanState == BleScanState.ACTIVE){
            return true
        }
        return false
    }
}