package com.ihrsachin.sensoreventmanageger.api_responses

data class User(
    val date_joined: String,
    val email: String,
    val id: Int,
    val is_active: Boolean,
    val last_login: Any,
    val username: String
)