package com.project_ips.piv0.ble_scanning

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

abstract class ScanningActivity : AppCompatActivity() {

    private val tag = this::class.java.simpleName

    val bluetoothScanPermission = 2

    lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                //  you will get result here in result.data
                Log.d(tag, result.data.toString())
            }
            else{
                //user cancelled turning on Bluetooth
                Log.d(tag, result.data.toString())
            }
        }
    }

    fun requestBleScan(){
        BleScanner.startScan(this)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onPause() {
        BleScanner.stopScanning()
        super.onPause()
    }

}