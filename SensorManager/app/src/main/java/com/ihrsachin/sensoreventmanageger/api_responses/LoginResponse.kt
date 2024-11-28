package com.ihrsachin.sensoreventmanageger.api_responses

data class LoginResponse(
    val access: String,
    val refresh: String,
    val user: User
)