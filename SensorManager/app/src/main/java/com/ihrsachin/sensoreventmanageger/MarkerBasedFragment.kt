package com.ihrsachin.sensoreventmanageger

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.gson.Gson
import com.ihrsachin.sensoreventmanageger.databinding.FragmentMarkerBasedBinding
import com.ihrsachin.sensoreventmanageger.models.*
import java.io.IOException
import java.util.ArrayList


class MarkerBasedFragment : Fragment() {
    private val TAG = "MarkerBased Fragment"
    private lateinit var binding: FragmentMarkerBasedBinding
    private lateinit var viewModel: MarkerBasedFragmentViewModel
    var testRunning = false
    private lateinit var testConfig: TestConfig

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_marker_based, container, false)
        binding.nextBtn.setOnClickListener{
            it.findNavController().navigate(R.id.action_markerBasedFragment_to_finalFragment)
        }
        viewModel = ViewModelProvider(this)[MarkerBasedFragmentViewModel::class.java]

        binding.mapView.setImageResource(R.drawable.test_config)
        testConfig = Gson().fromJson(activity?.applicationContext?.let { getJsonDataFromAsset(it, "maps/" + "test_config.json") },TestConfig::class.java)
        initializeMarkerPosition()
        initializeBeaconPosition()

        val integerArrayList = requireArguments().getParcelable<IntegerArrayList>("sensorCodeArray")
        val integerList = integerArrayList?.list

        for (i in integerList!!){
            Log.d("Sensor Selected", i.toString())
        }

        return  binding.root
    }
     private fun initializeBeaconPosition() {
        getBeaconPositionsFromConfigs(testConfig.JSONBeaconConfigs).let { binding.mapView.initializeBeaconPositions(it) }
        binding.mapView.invalidate()
    }
    private fun getBeaconPositionsFromConfigs(JSONBeaconConfigs : ArrayList<JSONBeaconConfig>) : ArrayList<Position2> {
        val arrayList = ArrayList<Position2>()
        for (beaconConfig in JSONBeaconConfigs){
            arrayList.add(Position2(beaconConfig.position.x,beaconConfig.position.y))
        }
        return arrayList
    }
    private fun getJsonDataFromAsset(context: Context, fileName: String): String? {
        Log.d("test", "getting JsonDataFromAsset")
        val jsonString: String
        try {
            jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }
        return jsonString
    }

     private fun initializeMarkerPosition() {
        // No markers for positioning
        Log.d(TAG, "initializeMarkerPosition")
        Log.d(TAG, "initializeMarkerPosition: $testConfig")
        testConfig.markerPositions.let { binding.mapView.initialiseMarkerPositions(it) }
        binding.mapView.invalidate()
    }


    //onStart

     fun onTestStart() {
        /**
         * When this observer is attached to button it will set it's text to the marker number.
         */
        val markerObserver = androidx.lifecycle.Observer<Int> { t -> binding.nextBtn.text = t.toString() }
        if (!testRunning) {
            Log.d(tag, "Test Started")
            //Disable this button for now. Will activate it when scanning is ready and started.
            binding.nextBtn.isEnabled = false
            startTest()
            //Attach marker observer to button. This observer is responsible for setting button text to marker number.
            //First time initialisation is done when attaching the observer.
            //TODO: This design ignores the lifecycle of the view. May leak if the view was interrupted.
            viewModel.currentMarker.observeForever(markerObserver)
            binding.nextBtn.isEnabled = true
            testRunning = true
        } else {
            if(viewModel.currentMarker.value!! == binding.mapView.markerPositions.size){
                //Call increment to record the final observation
                viewModel.incrementMarker()
                finishTest()

                viewModel.currentMarker.value = 0
                testRunning = false
                binding.nextBtn.text = getString(R.string.start_test)
                return
            }
            if(viewModel.currentMarker.value!! == binding.mapView.markerPositions.size - 1){
                viewModel.currentMarker.removeObserver(markerObserver) //TODO: IDK this doesn't work
                //Workaround because removeObserver is not working
                viewModel.incrementMarker()
                binding.nextBtn.text = getString(R.string.finish_test)
                return
            }
            viewModel.incrementMarker()
        }
    }

    private fun startTest() {
//        startScanning()
//============================== Pure Trilateration Approach ======================================//
//        //enableSensors()
//        BeaconManager.startScan()
//        TrilaterationManager.startTrilateration()

//        PositionEventPublisher.subscribe(object : PositionEventSubscriber {
//            override fun onPositionChanged(posUpdate: PositionUpdate) {
//                requireActivity().runOnUiThread { binding.mapView.onPositionUpdate(posUpdate.position.x,posUpdate.position.y) }
//                webSocket.sendMsg("{\"msg\":\"hi\",\"xCor\":${posUpdate.position.x},\"yCor\":${posUpdate.position.y},\"user\":\"$androidId\"}")
//            }
//            //TODO : Unsubscribe all observers as soon as fragment gets detached from activity.
//            //TODO : Or subscribe inside the view model.
//        })

//============================== Pure Dead Reckoning Approach ====================================//
        val startPos = Position3(22.0,5.0, 2.40)
        requireActivity().runOnUiThread { binding.mapView.onPositionUpdate(startPos.x,startPos.y) }
//        DRManager.startDeadReckoning(requireContext(), startPos,object : DRPositionUpdateListener {
//            override fun onPositionUpdateListener(newPosition: Position3, delX: Double, delY: Double) {
//                requireActivity().runOnUiThread { binding!!.mapView.onPositionUpdate(newPosition.x,newPosition.y) }
//                val message = "{\n" +
//                        "    \"device_id\":\"android_instance_1\",\n" +
//                        "    \"x\":${newPosition.x},\n" +
//                        "    \"y\":${newPosition.y},\n" +
//                        "    \"timestamp\":\"2023-03-26 12:35\",\n" +
//                        "    \"place_id\":\"1\"\n" +
//                        "}";
//                webSocket.sendMsg(message)
//            }
//            //TODO : Unsubscribe all observers as soon as fragment gets detached from activity.
//            //TODO : Or subscribe inside the view model.
//        })

//============================= FUSION APPROACH =================================================//
//        FusionManager.startFusion(requireActivity(), 1.25, 0.005, object: FusionPositionUpdateListener{
//            override fun onPositionUpdate(posUpdate: PositionUpdate) {
//                requireActivity().runOnUiThread { binding.mapView.onPositionUpdate(posUpdate.position.x,posUpdate.position.y) }
//            }
//        }, 500.0)
//        //TODO : Unsubscribe all observers as soon as fragment gets detached from activity.
//        //TODO : Or subscribe inside the view model.
    }

   private fun finishTest() {
//        stopScanning()
        //stop sensors' data recording
        //disableSensors()
        //TODO: Stop C++ here
//        var records = ""
//        //For holding temp value of marker index
//        var index = 0
        //key == timestamp in microseconds
        //value == marker number == 0...12
//        for ((key, value) in viewModel!!.markerRecords){
//            Log.d(tag, "$key to $value")
//            index = if (value == binding!!.mapView.markerPositions.size) 0 else value
//            records += "$key," +
//                    "$value," +
//                    "${binding!!.mapView.markerPositions[index].x}," +
//                    "${binding!!.mapView.markerPositions[index].y}," +
//                    "${getMeta()}\n"
//        }
        //Reinitialise to empty
        viewModel.markerRecords = LinkedHashMap()

        //write user position labels data to file
//        writeTestToFile("${getHeading()}\n" +
//                "$records\n","Marker Data.csv")
//        for (beaconDataset in beaconDatasets){
//            val fileName = "Beacon ${beaconDataset.kontakt_uid}.csv"
//            writeTestToFile(beaconDataset.toCsvString()+"\n",fileName)
//            //Now clear the datasets.
//            beaconDataset.clearData()
//        }
        //Give a toast on successful completion.
        Toast.makeText(this.context, "Recording Successful", Toast.LENGTH_SHORT).show()
        //Inserting data on MongoDb
        //MongoDB(requireContext()).insertData()
    }

    fun updateAcceleration(x: Float, y: Float, z: Float){
//        binding.x_value.text = x.toString()
    }


}