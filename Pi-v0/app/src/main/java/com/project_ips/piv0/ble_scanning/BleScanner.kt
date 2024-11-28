package com.project_ips.piv0.ble_scanning

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import com.project_ips.piv0.jni_models.BeaconCallback
import com.project_ips.piv0.jni_models.PositioningAlgorithm

object BleScanner{

    private val tag = javaClass.simpleName

    /**
     * Only an activity instance can start scanning. An activity instance is needed for[<br>]
     * 1. Get Bluetooth System Service : [getSystemService()]
     * 2. To enable Bluetooth Adapter via [registerForActivityResult()]
     * 3. Check/Request Bluetooth or Location Permissions at runtime
     */
    private var scanningActivity: ScanningActivity? = null

    /**
     * State variable[<br>]
     * true => BleScanning is running
     * false => otherwise
     */
    private var initialised: Boolean = false

    private var scanning = false

    private val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager = scanningActivity?.getSystemService(Context.BLUETOOTH_SERVICE) as
                BluetoothManager
        bluetoothManager.adapter
    }

    private val bluetoothLeScanner : BluetoothLeScanner? by lazy {
        bluetoothAdapter?.bluetoothLeScanner
    }

    private lateinit var scanCallback: ScanCallback

    private lateinit var bleThread: BleThread

    private val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings
        .SCAN_MODE_BALANCED).setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).setReportDelay(0).build()

    private const val HEXES = "0123456789ABCDEF"

    /**
     * The point where BLE Scanning begins.
     */
    fun startScan(activity: ScanningActivity) {
        scanningActivity = activity
        if(!initialised){
            //Initialise C++ algorithm
            PositioningAlgorithm.getInstance().create()
            PositioningAlgorithm.getInstance().begin(PositioningAlgorithm.ALGO_FLAG
                .Trilateration,0,0)
            bleInit()
            bleThread = BleThread(this)
            bleThread.start()
            initialised = true
        }
    }

    fun stopScanning() {
        bleThread.finish()
        stopSingleScan()
        bleThread.join()
        initialised = false
        Log.d(tag, "Quitting BLE Scan")
    }

    private fun bleInit(){
        if(bluetoothAdapter == null){
            //OMG No bluetooth is present
            return
        }
        if (bluetoothLeScanner == null){
            //OMG BLE not present
            return
        }
        scanCallback = object : ScanCallback(){
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                onScan(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                onScan(results)
            }
        }

        if(!bluetoothAdapter!!.isEnabled){
            scanningActivity?.resultLauncher?.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
        }
    }

    fun onScan(@Suppress("UNUSED_PARAMETER")callbackType: Int, result: ScanResult?){
        //Log.d(tag, result.toString())
        val bytes = result!!.scanRecord!!.bytes
//        Log.d(tag, "Printing Record :\n" +
//                "UUID : ${getUuid(bytes)}\n" +
//                "major : ${getMajor(bytes)}\n" +
//                "minor : ${getMinor(bytes)}")

        BeaconCallback.resolve(getUuid(bytes), getMajor(bytes)!!.toInt(16), getMinor(bytes)!!
        .toInt(16),result.rssi)
    }

    fun onScan(results : MutableList<ScanResult>?){
        if (results != null) {
            Log.d(tag, "onBatchScan Results")
            for (r in results){
                onScan(0,r)
            }
        }
    }

    fun startSingleScan(){
        if(!scanning){
            if (scanningActivity?.let {
                    ActivityCompat.checkSelfPermission(
                        it, Manifest.permission.BLUETOOTH_SCAN
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    scanningActivity?.let {
                        ActivityCompat.requestPermissions(
                            it, arrayOf(Manifest.permission
                                .BLUETOOTH_SCAN), scanningActivity!!.bluetoothScanPermission)
                    }
                }
            }
            bluetoothLeScanner?.startScan(null, scanSettings,scanCallback)
            scanning = true
        }
    }

    fun stopSingleScan(){
        if(scanning){
            if (scanningActivity?.let {
                    ActivityCompat.checkSelfPermission(
                        it,
                        Manifest.permission.BLUETOOTH_SCAN
                    )
                } != PackageManager.PERMISSION_GRANTED
            ) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    scanningActivity?.let {
                        ActivityCompat.requestPermissions(
                            it, arrayOf(Manifest.permission
                                .BLUETOOTH_SCAN), scanningActivity!!.bluetoothScanPermission)
                    }
                }
            }
            bluetoothLeScanner?.stopScan(scanCallback)
            scanning = false
        }
    }

    private fun getUuid(raw: ByteArray?): String? {
        if (raw == null || raw.size < 24) {
            return null
        }
        val uuid = ByteArray(16)
        System.arraycopy(raw, 9, uuid, 0, 16)
        return getHex(uuid)
    }

    private fun getMajor(raw: ByteArray?): String? {
        if (raw == null || raw.size < 27) {
            return null
        }
        val majorbytes = ByteArray(2)
        majorbytes[0] = raw[25]
        majorbytes[1] = raw[26]
        return getHex(majorbytes)
    }

    private fun getMinor(raw: ByteArray?): String? {
        if (raw == null || raw.size < 29) {
            return null
        }
        val minorbytes = ByteArray(2)
        minorbytes[0] = raw[27]
        minorbytes[1] = raw[28]
        return getHex(minorbytes)
    }

    private fun getHex(raw: ByteArray?): String? {
        if (raw == null) {
            return null
        }
        val hex = StringBuilder(2 * raw.size)
        for (b in raw) {
            hex.append(String.format("%02X", b))
        }
        return hex.toString()
    }
}
