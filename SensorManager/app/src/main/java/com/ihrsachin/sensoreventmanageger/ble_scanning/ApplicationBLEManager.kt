package com.ihrsachin.sensoreventmanageger.ble_scanning

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import com.ihrsachin.sensoreventmanageger.ble_scanning.BleAdapterState



/**
 * This class is the single source of truth for all Bluetooth operations. You do not need to access
 * this class directly instead extend [BleScanFragment].
 */
class ApplicationBLEManager(application: Application) : AndroidViewModel(application) {

    var adapterState : BleAdapterState

    var scanState: BleScanState

    private val tag = this::class.java.simpleName

    private val bleBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action

            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                Log.d(tag, "Broadcast Receiver : ${intent.getIntExtra(BluetoothAdapter
                    .EXTRA_STATE, -1)}")

                when(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1)){
                    BluetoothAdapter.STATE_ON -> {
                        adapterState = BleAdapterState.TURNED_ON
                        Log.d(tag, "Bluetooth state is on")
//                        app.startActivity(Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                    BluetoothAdapter.STATE_TURNING_ON -> {
                        adapterState = BleAdapterState.TURNING_ON
                        Log.d(tag, "Bluetooth is turning on")
                    }
                    BluetoothAdapter.STATE_TURNING_OFF -> {
                        adapterState = BleAdapterState.TURNING_OFF
                        Log.d(tag, "Bluetooth is turning off")
                        //Not good may give an alert or do some cleaning up
                    }
                    BluetoothAdapter.STATE_OFF -> {
                        adapterState = BleAdapterState.TURNED_OFF
                        Log.d(tag, "Bluetooth is off")
//                        app.startActivity(Intent(context, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
                    }
                    else -> {
                        adapterState = BleAdapterState.NO_BLE
                        Log.d(tag, "Invalid Bluetooth state received")
                    }
                }
            }
        }
    }

    val bluetoothAdapter: BluetoothAdapter? by lazy {
        val bluetoothManager =
            app.getSystemService(Context.BLUETOOTH_SERVICE) as
                    BluetoothManager
        bluetoothManager.adapter
    }

    private val bleScanner : BleScanner? by lazy {
        BleScanner(this)
    }

    private val app = application

    val iBeaconCallbackSubscribers = ArrayList<IBeaconCallbackSubscriber>()

    init {
        application.registerReceiver(bleBroadcastReceiver, IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        Log.d(tag, "Broadcast receiver registered")
        adapterState = getBleState()
        scanState = BleScanState.INACTIVE
    }

    private fun getBleState() : BleAdapterState {
        if(bluetoothAdapter == null){
            return BleAdapterState.NO_BLE
        }
        return if(bluetoothAdapter?.isEnabled == true){
            BleAdapterState.TURNED_ON
        } else{
            BleAdapterState.TURNED_OFF
        }
    }

    fun startBleScan(activity: Activity){
        if (adapterState == BleAdapterState.TURNED_ON){
            if(bleScanner?.startScan(activity) == true){
                scanState = BleScanState.ACTIVE
            }
        }
        else{
            Log.d(tag, "Cannot run Ble Scan. Adapter is turned off")
        }
    }

    fun stopBleScan(){
        if(bleScanner?.stopScanning() == true){
            scanState = BleScanState.INACTIVE
        }
    }

    fun registerIBeaconCallbackSubscriber(iBeaconCallbackSubscriber: IBeaconCallbackSubscriber){
        iBeaconCallbackSubscribers.add(iBeaconCallbackSubscriber)
    }

    @Suppress("unused")
    fun removeIBeaconCallbackSubscriber(iBeaconCallbackSubscriber: IBeaconCallbackSubscriber){
        iBeaconCallbackSubscribers.remove(iBeaconCallbackSubscriber)
    }

}

private class BleScanner(private val applicationBLEManager: ApplicationBLEManager){

    private val tag = javaClass.simpleName

    private var scanning = false

    private var initialized = false

    private lateinit var scanCallback: ScanCallback

    private lateinit var cycledBleThreadScanner: CycledBleThreadScanner

    private var scanningActivity: Activity? = null

    private val bluetoothScanPermission = 2
    private val bluetoothLeScanner: BluetoothLeScanner? by lazy {
        applicationBLEManager.bluetoothAdapter?.bluetoothLeScanner
    }

    private val scanSettings = ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
        .setMatchMode(ScanSettings.MATCH_MODE_AGGRESSIVE).setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
        .setReportDelay(0).build()

    /**
     * The point where BLE Scanning begins.
     */
    fun startScan(activity: Activity) : Boolean {
        if(!initialized) {
            bleInit()
            initialized = true
        }
        scanningActivity = activity
        if(!scanning){
            cycledBleThreadScanner = CycledBleThreadScanner(this)
            cycledBleThreadScanner.start()
            //scanning = true
            Log.d(tag, "Starting BLE Scan")
        }
        return scanning
    }

    fun stopScanning() : Boolean{
        if(scanning){
            cycledBleThreadScanner.finish()
            stopSingleScan()
            cycledBleThreadScanner.join()
            scanning = false
            scanningActivity = null
            Log.d(tag, "Quitting BLE Scan")
        }
        return !scanning
    }

    fun bleInit(){
        scanCallback = object : ScanCallback(){
            override fun onScanResult(callbackType: Int, result: ScanResult?) {
                onScan(callbackType, result)
            }

            override fun onBatchScanResults(results: MutableList<ScanResult>?) {
                onScan(results)
            }
        }
        Log.d(tag, "Scan Callback Initialised")
    }

    fun onScan(@Suppress("UNUSED_PARAMETER")callbackType: Int, result: ScanResult?){
        //Log.d(tag, result.toString())
        val bytes = result?.scanRecord?.bytes
        if (result?.scanRecord?.deviceName == "Kontakt"){
            applicationBLEManager.iBeaconCallbackSubscribers.forEach {
                it.onIBeaconCallbackReceived(getUuid(bytes)!!,getMajor(bytes)!!,getMinor(bytes)
                !!, result.rssi, bytes?.get(29)?.toInt()!!, result.scanRecord!!.txPowerLevel)
            }
        }
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
                            it, arrayOf(
                                Manifest.permission
                                    .BLUETOOTH_SCAN), bluetoothScanPermission)
                    }
                }
            }
            bluetoothLeScanner?.startScan(null, scanSettings, scanCallback)
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
                            it, arrayOf(
                                Manifest.permission
                                    .BLUETOOTH_SCAN), bluetoothScanPermission)
                    }
                }
            }
            bluetoothLeScanner?.stopScan(scanCallback)
            scanning = false
        }
    }

    private fun getUuid(raw: ByteArray?): String? {
        if (raw == null || raw.size < 25) {
            return null
        }
        val uuid = ByteArray(16)
        System.arraycopy(raw, 9, uuid, 0, 16)
        return getHex(uuid)
    }

    private fun getMajor(raw: ByteArray?): Int?{
        return getMajorHex(raw)?.toInt(16)
    }

    private fun getMinor(raw: ByteArray?): Int?{
        return getMinorHex(raw)?.toInt(16)
    }

    @Suppress("unused")
    private fun getRssiAt1m(raw: ByteArray?) : Int?{
        return getRssiAt1mHex(raw)?.toInt(16)
    }

    private fun getMajorHex(raw: ByteArray?): String? {
        if (raw == null || raw.size < 27) {
            return null
        }
        val majorBytes = ByteArray(2)
        majorBytes[0] = raw[25]
        majorBytes[1] = raw[26]
        return getHex(majorBytes)
    }

    private fun getMinorHex(raw: ByteArray?): String? {
        if (raw == null || raw.size < 29) {
            return null
        }
        val minorBytes = ByteArray(2)
        minorBytes[0] = raw[27]
        minorBytes[1] = raw[28]
        return getHex(minorBytes)
    }

    private fun getRssiAt1mHex(raw: ByteArray?): String?{
        if (raw == null || raw.size < 30) {
            return null
        }
        val rssiAt1mByte = ByteArray(1)
        rssiAt1mByte[0] = raw[29]
        return getHex(rssiAt1mByte)
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

private class CycledBleThreadScanner(bleScanner: BleScanner) : Thread() {
    private val tag = this::class.java.simpleName

    private val scanner = bleScanner
    private var counter = 0

    private var mRunning = true

    override fun run() {
        while(mRunning){
            counter++
            if (counter > 1) {
                if (counter % 15 == 0) {
                    scanner.stopSingleScan()
                    Log.d(tag, "Stopping Single Scan $counter")
                }
            }
            if (mRunning) {
                if (counter == 1 || counter % 15 == 0) {
                    scanner.startSingleScan()
                    Log.d(tag, "Starting Single Scan $counter")
                }
            }
            sleep(1000)
        }
    }

    @Synchronized
    override fun start() {
        mRunning = true
        super.start()
    }

    @Synchronized
    fun finish(){
        mRunning = false
    }


}