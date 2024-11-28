package com.ihrsachin.sensoreventmanageger

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ihrsachin.sensoreventmanageger.base.BaseFragment
import com.ihrsachin.sensoreventmanageger.databinding.FragmentLoginBinding
import com.ihrsachin.sensoreventmanageger.network.AuthAPI
import com.ihrsachin.sensoreventmanageger.network.Resource
import com.ihrsachin.sensoreventmanageger.repository.AuthRepository
import com.ihrsachin.sensoreventmanageger.utility.enable
import com.ihrsachin.sensoreventmanageger.utility.handleApiError
import com.ihrsachin.sensoreventmanageger.utility.startNewActivity
import com.ihrsachin.sensoreventmanageger.utility.visible
import kotlinx.coroutines.launch

class LoginFragment : BaseFragment<AuthViewModel, FragmentLoginBinding, AuthRepository>() {

    companion object {
        fun newInstance() = LoginFragment()
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.progressBar.visible(false)
        binding.loginButton.enable(false)

        viewModel.loginResponse.observe(viewLifecycleOwner, Observer {
            binding.progressBar.visible(it is Resource.Loading)
            when(it){
                is Resource.Success -> {
                    //using lifecycleScope the NewActivity will be called
                    // only after save function finishes.
                    // lifecycleScope provides sequential function call inside it

                    lifecycleScope.launch {
                        viewModel.saveLoginAuthToken(it.value.toString())
                        requireActivity().startNewActivity(HomeActivity::class.java)
                    }
                }
                is Resource.Failure -> handleApiError(it){login()}
                else -> {}
            }
        })

        binding.signUp.setOnClickListener{
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }

        binding.emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.loginButton.setOnClickListener{
            login()
        }

        binding.passwordEditText.addTextChangedListener{
            val email = binding.emailEditText.text.toString().trim()
            binding.loginButton.enable(email.isNotEmpty() && it.toString().isNotEmpty())
        }
    }

    private fun login(){
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        viewModel.login(email, password)
    }
    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentLoginBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository((remoteDataSource.buildApi(AuthAPI::class.java)), userPreferences )


}