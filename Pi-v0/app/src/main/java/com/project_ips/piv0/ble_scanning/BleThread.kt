package com.project_ips.piv0.ble_scanning

import android.util.Log

class BleThread(bleScanner: BleScanner) : Thread() {

    private val tag = this::class.java.simpleName

    private val scanner = bleScanner
    private var counter = 0

    private var mRunning = true

    override fun run() {
        while(mRunning){
            sleep(1000)
            counter++
            if (counter > 1) {
                if (counter % 15 == 0) {
                    scanner.stopSingleScan()
                    Log.d(tag, "Stopping Single Scan")
                }
            }
            if (mRunning) {
                if (counter == 1 || counter % 15 == 0) {
                    scanner.startSingleScan()
                    Log.d(tag, "Starting Single Scan")
                }
            }
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