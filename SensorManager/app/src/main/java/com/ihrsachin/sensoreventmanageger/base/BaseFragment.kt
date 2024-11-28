package com.ihrsachin.sensoreventmanageger.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.ihrsachin.sensoreventmanageger.AuthActivity
import com.ihrsachin.sensoreventmanageger.UserPreferences
import com.ihrsachin.sensoreventmanageger.network.RemoteDataSource
import com.ihrsachin.sensoreventmanageger.utility.startNewActivity
import kotlinx.coroutines.launch

abstract class BaseFragment<VM: BaseViewModel, B: ViewBinding, R: BaseRepository> : Fragment() {

    protected  lateinit var binding : B
    protected  val remoteDataSource = RemoteDataSource()
    protected lateinit var viewModel: VM
    protected lateinit var userPreferences: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //for instantiating a ViewModel create a ViewModelFactory instance & then get ViewModel

        userPreferences = UserPreferences(requireContext())

        val factory = ViewModelFactory(getFragmentRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        binding = getFragmentBinding(inflater, container)
        return  binding.root
    }

    abstract  fun  getViewModel() : Class<VM>

    abstract fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?) : B

    abstract  fun getFragmentRepository() : R

    fun logout() = lifecycleScope.launch {
        //@todo For Now I don't have to send any logout request to server API
//        val authToken = userPreferences.authLoginToken.first()
//        val api = remoteDataSource.buildApi(UserApi::class.java, authToken)
//
//        viewModel.logout(api)
        userPreferences.clear()
        requireActivity().startNewActivity(AuthActivity::class.java)
    }
}