package com.ihrsachin.sensoreventmanageger

import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.ihrsachin.sensoreventmanageger.base.BaseFragment
import com.ihrsachin.sensoreventmanageger.databinding.FragmentSignupBinding
import com.ihrsachin.sensoreventmanageger.network.AuthAPI
import com.ihrsachin.sensoreventmanageger.network.Resource
import com.ihrsachin.sensoreventmanageger.repository.AuthRepository
import com.ihrsachin.sensoreventmanageger.utility.enable
import com.ihrsachin.sensoreventmanageger.utility.handleApiError
import com.ihrsachin.sensoreventmanageger.utility.visible
import kotlinx.coroutines.launch

class SignupFragment : BaseFragment<AuthViewModel, FragmentSignupBinding, AuthRepository>() {

    companion object {
        fun newInstance() = SignupFragment()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.progressBar.visible(false)
        binding.signUpButton.enable(false)

        viewModel.signUpResponse.observe(viewLifecycleOwner) {
            binding.progressBar.visible(it is Resource.Loading)
            when (it) {
                is Resource.Success -> {
                    lifecycleScope.launch {
                        viewModel.saveRegisterAuthToken(it.value.toString())

                        val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
                        findNavController().navigate(action)
                    }

                }
                is Resource.Failure -> handleApiError(it){signUp()}
                else -> {}
            }
        }

        binding.signIn.setOnClickListener{
            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS

        binding.passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD

        binding.signUpButton.setOnClickListener{
            signUp()
        }

        binding.passwordEditText.addTextChangedListener{
            val user = binding.usernameEditText.text.toString().trim()
            val email = binding.emailEditText.text.toString().trim()
            binding.signUpButton.enable(user.isNotEmpty() && email.isNotEmpty() && it.toString().isNotEmpty())
        }

    }

    private fun signUp(){
        val user = binding.usernameEditText.text.toString().trim()
        val email = binding.emailEditText.text.toString().trim()
        val password = binding.passwordEditText.text.toString().trim()

        binding.progressBar.visible(true)
        viewModel.signUp(user, email, password)
    }

    override fun getViewModel() = AuthViewModel::class.java

    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ) = FragmentSignupBinding.inflate(inflater, container, false)

    override fun getFragmentRepository() = AuthRepository((remoteDataSource.buildApi(AuthAPI::class.java)), userPreferences )

}