package com.ihrsachin.sensoreventmanageger.api_responses

import com.ihrsachin.sensoreventmanageger.api_responses.UserX

data class SignUpResponse(
    val refresh: String,
    val token: String,
    val user: UserX
)