package com.ihrsachin.sensoreventmanageger.network

import okhttp3.ResponseBody
import retrofit2.http.GET

interface UserApi {

    @GET("logout")
    suspend fun logout(): ResponseBody
}