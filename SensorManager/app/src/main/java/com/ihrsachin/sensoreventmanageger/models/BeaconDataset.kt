package com.ihrsachin.sensoreventmanageger.models

import java.time.LocalDate

data class BeaconDataset constructor(val kontakt_uid : String, val uuid: String, val major : Int, val minor : Int,
                                     val place_of_recording : String, val type_of_test : String){
    override fun equals(other: Any?): Boolean = BeaconDatasetIdentifier(this) == (other as? BeaconDataset)?.let { BeaconDatasetIdentifier(it) }
    override fun hashCode() = BeaconDatasetIdentifier(this).hashCode()

    private val tag = BeaconDataset::class.java.simpleName

    private val dataPoints = mutableListOf<BeaconDataPoint>()

    fun addDataPoint(timestamp: Long, rssi: Int, rssi_1m: Int, tx_power: Int){
//        Log.d(tag,"Added $major,$minor,$rssi")
        dataPoints.add(BeaconDataPoint(timestamp, rssi, rssi_1m, tx_power))
    }

    fun addDataPoint(timestamp: Long, rssi: Int, rssi_1m: Int, tx_power: Int, temp : Double, humid: Double){
//        Log.d(tag,"Added $major,$minor,$rssi, $temp, $humid")
        dataPoints.add(BeaconDataPoint(timestamp, rssi, rssi_1m, tx_power, temp, humid))
    }

    fun toCsvString() : String{
        var csvString = getHeading()
        val dateOfRecording = LocalDate.now().toString()
        for (dataPoint in dataPoints){
            csvString += "${dataPoint.timestamp},$kontakt_uid,$uuid,$major,$minor," +
                    "${dataPoint.rssi},${dataPoint.rssi_1m},${dataPoint.tx_power}," +
                    "${dataPoint.temp},${dataPoint.humid}" +
                    "$place_of_recording,$type_of_test,$dateOfRecording\n"
        }
        return csvString
    }

    fun clearData(){
        dataPoints.clear()
    }

    private fun getHeading() : String{
        val columnNames = ArrayList<String>()
        columnNames.add("Timestamp (microseconds)")
        columnNames.add("Kontakt UID")
        columnNames.add("UUID")
        columnNames.add("Major")
        columnNames.add("Minor")
        columnNames.add("Rssi")
        columnNames.add("Rssi@1m")
        columnNames.add("Tx Power")
        columnNames.add("Temperature")
        columnNames.add("Relative Humidity")
        columnNames.add("Place of Recording")
        columnNames.add("Type of Test")
        columnNames.add("Date of Recording")

        var header = ""
        for (columnName in columnNames){
            header+="$columnName,"
        }
        return header+"\n"
    }
}

private data class BeaconDataPoint(val timestamp : Long, val rssi : Int, val rssi_1m : Int, val tx_power : Int, val temp: Double =0.0, val humid: Double=0.0)

private data class BeaconDatasetIdentifier(val uuid: String, val major : Int, val minor : Int){
    constructor(beaconDataset : BeaconDataset) : this(uuid = beaconDataset.uuid, major = beaconDataset.major, minor = beaconDataset.minor)
}