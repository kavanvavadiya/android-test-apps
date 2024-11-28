package com.ihrsachin.sensoreventmanageger

import android.hardware.SensorEvent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.ihrsachin.sensoreventmanageger.databinding.FragmentRecordDataBinding
import com.ihrsachin.sensoreventmanageger.http_request.MapHttpRequest
import com.ihrsachin.sensoreventmanageger.models.Destination
import com.ihrsachin.sensoreventmanageger.models.IntegerArrayList
import com.ihrsachin.sensoreventmanageger.models.SensorType


class RecordDataFragment : Fragment() {
    private val TAG = "RecordFragment"
    private lateinit var binding: FragmentRecordDataBinding
    private lateinit var viewModel: RecordDataFragmentViewModel
    private lateinit var selectedItem: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        var record = arrayListOf<String?>("Step Recording", "Marker Based Recording")
//        val reacord_adepter = ArrayAdapter(this,R.layout)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_record_data, container, false)
        viewModel = ViewModelProvider(requireActivity())[RecordDataFragmentViewModel::class.java]

        /**
         * Make GET request, update list of maps
         */

        val recordType = arrayOf("Step Recording", "Marker Based Recording")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line,recordType)
//        autoCompleteTextView.setAdapter(adapter)
//        val adapter = DropDownAdapter(requireContext(), viewModel.record.value!!)
        binding.availableRecordType.setAdapter(adapter)
        binding.availableRecordType.setOnItemClickListener { parent, view, position, id ->
             selectedItem = parent.getItemAtPosition(position).toString()
        }
        val sensorsCode : ArrayList<Int> = arrayListOf()

        binding.btnGo.setOnClickListener{
            if(binding.Accel.isChecked) sensorsCode.add(SensorType.ACCELEROMETER.value)
            if(binding.Gyro.isChecked) sensorsCode.add(SensorType.GYROSCOPE.value)
            if(binding.Mag.isChecked) sensorsCode.add(SensorType.MAGNETIC_FIELD.value)
            if(binding.Temp.isChecked) sensorsCode.add(SensorType.AMBIENT_TEMPERATURE.value)

            when (selectedItem) {
                "Step Recording" -> {
                    val action = RecordDataFragmentDirections.actionRecordDataFragmentToStepRecordingFragment(
                        IntegerArrayList(sensorsCode)
                    )
                    findNavController().navigate(action)
//                    findNavController().navigate(R.id.action_recordDataFragment_to_stepRecordingFragment)
                }
                "Marker Based Recording" -> {
                    val action = RecordDataFragmentDirections.actionRecordDataFragmentToMarkerBasedFragment(
                        IntegerArrayList(sensorsCode)
                    )
                    findNavController().navigate(action)
//                    findNavController().navigate(R.id.action_recordDataFragment_to_markerBasedFragment)
                }
                else -> {
                    Log.d(TAG, "Error Occurred, couldn't decide destination")
                }
            }

        }


        return  binding.root
    }


}