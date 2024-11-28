package com.project_ips.piv0

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.core.content.res.ResourcesCompat
import androidx.core.os.HandlerCompat
import androidx.databinding.DataBindingUtil
import com.project_ips.piv0.ble_scanning.ScanningActivity
import com.project_ips.piv0.databinding.ActivityMainBinding
import com.project_ips.piv0.jni_models.ConfiguredBeacon
import com.project_ips.piv0.jni_models.Filter
import com.project_ips.piv0.jni_models.MeanFilter
import java.util.*
import kotlin.concurrent.thread

class MainActivity : ScanningActivity() {

    private val tag = this::class.java.simpleName

    val mainThreadHandler: Handler = HandlerCompat.createAsync(Looper.getMainLooper())

    companion object UPDATE{
        lateinit  var context : Activity
        lateinit var positionUpdate: PositionUpdate
        init {
            System.loadLibrary("piv0")
        }

        fun onPositionUpdate(x : Int, y: Int){
            context.runOnUiThread { positionUpdate.onPositionUpdate(x, y) }
        }
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mapView.setImageDrawable(
            ResourcesCompat.getDrawable(resources,R.drawable
            .ic_test_tl,null))

        positionUpdate = PositionUpdate(binding.mapView)
        context = this
        //simulatePositionUpdates()
        initialiseConfiguredBeacons()
    }

    override fun onResume() {
        super.onResume()
        requestBleScan()
    }

    private fun initialiseConfiguredBeacons(){
        val beaconCa8t: ConfiguredBeacon.Builder = ConfiguredBeacon.Builder.allocate()
        var f: Filter? = null
        f = MeanFilter.allocate(3)
        beaconCa8t.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            41295,
            22532
        ).setPos(2191, 959, 178).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f)

        ConfiguredBeacon.addToConfiguredBeacons(beaconCa8t.build())
        Filter.free(f)
        ConfiguredBeacon.Builder.free(beaconCa8t)

        val builder5Pe9 = ConfiguredBeacon.Builder.allocate()
        var f2: Filter? = null
        f2 = MeanFilter.allocate(3)
        builder5Pe9.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            52282,
            25550
        ).setPos(1491, 1471, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f2)

        ConfiguredBeacon.addToConfiguredBeacons(builder5Pe9.build())
        Filter.free(f2)
        ConfiguredBeacon.Builder.free(builder5Pe9)

        val builderTRn5 = ConfiguredBeacon.Builder.allocate()
        var f3: Filter? = null
        f3 = MeanFilter.allocate(3)
        builderTRn5.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            33752,
            56706
        ).setPos(2018, -1471, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f3)

        ConfiguredBeacon.addToConfiguredBeacons(builderTRn5.build())
        Filter.free(f3)
        ConfiguredBeacon.Builder.free(builderTRn5)

        val builderUBuc = ConfiguredBeacon.Builder.allocate()
        var f4: Filter? = null
        f4 = MeanFilter.allocate(3)
        builderUBuc.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            53652,
            14601
        ).setPos(1227, 1471, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f4)

        ConfiguredBeacon.addToConfiguredBeacons(builderUBuc.build())
        Filter.free(f4)
        ConfiguredBeacon.Builder.free(builderUBuc)

        val builderBnU4 = ConfiguredBeacon.Builder.allocate()
        var f5: Filter? = null
        f5 = MeanFilter.allocate(3)
        builderBnU4.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            15804,
            50863
        ).setPos(1927, 959, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f5)

        ConfiguredBeacon.addToConfiguredBeacons(builderBnU4.build())
        Filter.free(f5)
        ConfiguredBeacon.Builder.free(builderBnU4)

        val builderJOgm = ConfiguredBeacon.Builder.allocate()
        var f6: Filter? = null
        f6 = MeanFilter.allocate(3)
        builderJOgm.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            35378,
            59115
        ).setPos(1399, 959, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f6)

        ConfiguredBeacon.addToConfiguredBeacons(builderJOgm.build())
        Filter.free(f6)
        ConfiguredBeacon.Builder.free(builderJOgm)

        val builderOFG8 = ConfiguredBeacon.Builder.allocate()
        var f7: Filter? = null
        f7 = MeanFilter.allocate(3)
        builderOFG8.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            54474,
            40356
        ).setPos(1663, 959, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f7)

        ConfiguredBeacon.addToConfiguredBeacons(builderOFG8.build())
        Filter.free(f7)
        ConfiguredBeacon.Builder.free(builderOFG8)

        val builder1Wcd = ConfiguredBeacon.Builder.allocate()
        var f8: Filter? = null
        f8 = MeanFilter.allocate(3)
        builder1Wcd.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            63792,
            53955
        ).setPos(963, 1471, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f8)

        ConfiguredBeacon.addToConfiguredBeacons(builder1Wcd.build())
        Filter.free(f8)
        ConfiguredBeacon.Builder.free(builder1Wcd)

        val builderWKpC = ConfiguredBeacon.Builder.allocate()
        var f9: Filter? = null
        f9 = MeanFilter.allocate(3)
        builderWKpC.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            46710,
            32451
        ).setPos(832, 959, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f9)

        ConfiguredBeacon.addToConfiguredBeacons(builderWKpC.build())
        Filter.free(f9)
        ConfiguredBeacon.Builder.free(builderWKpC)

        val builderX2s7 = ConfiguredBeacon.Builder.allocate()
        var f10: Filter? = null
        f10 = MeanFilter.allocate(3)
        builderX2s7.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            11030,
            43064
        ).setPos(1095, 959, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f10)

        ConfiguredBeacon.addToConfiguredBeacons(builderX2s7.build())
        Filter.free(f10)
        ConfiguredBeacon.Builder.free(builderX2s7)

        val builderyaSN = ConfiguredBeacon.Builder.allocate()
        var f11: Filter? = null
        f11 = MeanFilter.allocate(3)
        builderyaSN.setId(
            "b2dd3555ea394f08862a00fb026a800b".uppercase(Locale.getDefault()),
            14132,
            17892
        ).setPos(1754, 1471, 240).setRssiD0(-77).setD0(100).setBeaconCoeff(1F).setXSigma(0F)
            .registerFilter(f11)

        ConfiguredBeacon.addToConfiguredBeacons(builderyaSN.build())
        Filter.free(f11)
        ConfiguredBeacon.Builder.free(builderyaSN)
    }

    private fun simulatePositionUpdates(){
        thread(start = true) {
            println("${Thread.currentThread()} started.")
            for (i in 1..5){
                positionUpdate.onPositionUpdate(20,20+i*20)
                Thread.sleep(1000)
            }
        }
    }
}

