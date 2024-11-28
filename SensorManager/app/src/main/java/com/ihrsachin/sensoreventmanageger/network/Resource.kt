package com.ihrsachin.sensoreventmanageger.network

import okhttp3.ResponseBody


// Creating class to handle API response from the server
// separate data class for handling success & failure

sealed class Resource<out T> {
    data class Success<out T>(val value: T): Resource<T>()
    data class Failure(
        val isNetworkError: Boolean,
        val errorCode: Int?,
        val errorBody: ResponseBody?
    ): Resource<Nothing>()
    object  Loading : Resource<Nothing>()
}