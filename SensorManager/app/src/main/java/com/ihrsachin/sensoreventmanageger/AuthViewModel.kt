package com.ihrsachin.sensoreventmanageger

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ihrsachin.sensoreventmanageger.api_responses.LoginResponse
import com.ihrsachin.sensoreventmanageger.api_responses.SignUpResponse
import com.ihrsachin.sensoreventmanageger.base.BaseViewModel
import com.ihrsachin.sensoreventmanageger.network.Resource
import com.ihrsachin.sensoreventmanageger.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository
) : BaseViewModel(repository) {

    private val _loginResponse : MutableLiveData<Resource<LoginResponse>> = MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>>
        get() = _loginResponse

    private val _signUpResponse : MutableLiveData<Resource<SignUpResponse>> = MutableLiveData()
    val signUpResponse: LiveData<Resource<SignUpResponse>>
        get() = _signUpResponse

    fun login(
        email: String,
        password: String
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _loginResponse.value = repository.login(email, password)
    }

    fun signUp(
        user: String,
        email: String,
        password: String
    ) = viewModelScope.launch {
        _loginResponse.value = Resource.Loading
        _signUpResponse.value = repository.signUp(user, email, password)
    }

    fun saveregisterAuthToken(token: String) = viewModelScope.launch {

    }

    suspend fun saveLoginAuthToken(token: String) {
        repository.saveLoginAuthToken(token)
    }

    fun getLoginToken() : Flow<String?> = repository.getLoginToken()

    suspend fun saveRegisterAuthToken(token: String) {
        repository.saveRegisterAuthToken(token)
    }

    fun getRegisterToken() : Flow<String?> = repository.getRegisterToken()
}