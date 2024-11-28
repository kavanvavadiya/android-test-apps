package com.ihrsachin.sensoreventmanageger

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.lifecycle.lifecycleScope
import com.ihrsachin.sensoreventmanageger.databinding.FragmentHomeBinding
import com.ihrsachin.sensoreventmanageger.utility.startNewActivity
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var userPreferences: UserPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        userPreferences = UserPreferences(requireContext())

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.logoutButton.setOnClickListener{
            logout()
        }
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_home, container, false)
        binding.recordData.setOnClickListener{
            it.findNavController().navigate(R.id.action_homeFragment_to_recordDataFragment)
        }
        binding.downloadData.setOnClickListener {
            it.findNavController().navigate(R.id.action_homeFragment_to_downloadDataFragment)
        }
        return  binding.root
    }

    private fun logout() = lifecycleScope.launch {
        userPreferences.clear()
        requireActivity().startNewActivity(AuthActivity::class.java)
    }

}