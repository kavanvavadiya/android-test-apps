package com.ihrsachin.sensoreventmanageger.repository

import com.ihrsachin.sensoreventmanageger.UserPreferences
import com.ihrsachin.sensoreventmanageger.base.BaseRepository
import com.ihrsachin.sensoreventmanageger.network.AuthAPI
import kotlinx.coroutines.flow.Flow

class AuthRepository(
    private val api: AuthAPI,
    private val preferences: UserPreferences
): BaseRepository(){

    suspend fun login(
        email: String,
        password: String
    ) = safeApiCall {
        api.login(email,password)
    }

    suspend fun signUp(
        user: String,
        email: String,
        password: String
    ) = safeApiCall {
        api.signUp(user, email, password)
    }

    suspend fun saveLoginAuthToken(token: String){
        preferences.saveLoginAuthToken(token)
    }

    fun getLoginToken(): Flow<String?> {
        return preferences.authLoginToken
    }

    suspend fun saveRegisterAuthToken(token: String){
        preferences.saveRegisterAuthToken(token)
    }

    fun getRegisterToken(): Flow<String?> {
        return preferences.authRegisterToken
    }
}