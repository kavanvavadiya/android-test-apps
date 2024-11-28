package com.ihrsachin.sensoreventmanageger

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import com.ihrsachin.sensoreventmanageger.databinding.FragmentStepRecordingBinding
import com.ihrsachin.sensoreventmanageger.jni_methods.JNI
import com.ihrsachin.sensoreventmanageger.models.IntegerArrayList

class StepRecordingFragment : Fragment() {
    private lateinit var binding: FragmentStepRecordingBinding
    private lateinit var viewModel: StepRecordingFragmentViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_step_recording, container, false)
        binding.recordBtn.setOnClickListener {
            if (binding.recordBtn.text == "Start Recording") {
                binding.recordBtn.text = "Stop Recording"
            } else {
                it.findNavController().navigate(R.id.action_stepRecordingFragment_to_finalFragment)
            }
        }

            val integerArrayList =
                requireArguments().getParcelable<IntegerArrayList>("sensorCodeArray")
            val integerList = integerArrayList?.list

            for (i in integerList!!) {
                Log.d("Sensor Selected", i.toString())
            }
            viewModel = ViewModelProvider(this).get(StepRecordingFragmentViewModel::class.java)
            viewModel.count.observe(viewLifecycleOwner, Observer {
                if (it >= 10) {
                    binding.countText.text = it.toString()
                } else {
                    binding.countText.text = "0" + it.toString()
                }
            })
            binding.nextStep.setOnClickListener {
                Log.d("Step","Enter in next step")
                viewModel.updateCount()
            }


        return binding.root
    }
}