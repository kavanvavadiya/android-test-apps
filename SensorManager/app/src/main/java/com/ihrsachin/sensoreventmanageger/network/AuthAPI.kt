package com.ihrsachin.sensoreventmanageger.network

import com.ihrsachin.sensoreventmanageger.api_responses.LoginResponse
import com.ihrsachin.sensoreventmanageger.api_responses.SignUpResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

//creating the Client side API interface

interface AuthAPI {

    @FormUrlEncoded
    @POST("login/")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ) : LoginResponse

    @FormUrlEncoded
    @POST("register/")
    suspend fun signUp(
        @Field("username") user: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : SignUpResponse
}