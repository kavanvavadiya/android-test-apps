package com.ihrsachin.sensoreventmanageger.base

import com.ihrsachin.sensoreventmanageger.network.Resource
import com.ihrsachin.sensoreventmanageger.network.UserApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException


//suspend function is to implement asynchronous in the function

abstract class BaseRepository {

    //it will execute API call
    // pass a parameter that is api call

    // error handling using Resource class

    suspend fun <T> safeApiCall(
        apiCall: suspend () -> T
    ): Resource<T> {
        return  withContext(Dispatchers.IO){//thread pool optimized for I/O operations
            try {
                Resource.Success(apiCall.invoke())
            }
            catch (throwable: Throwable){
                when(throwable){
                    is HttpException -> {
                        Resource.Failure(false, throwable.code(), throwable.response()?.errorBody())
                    }
                    else -> {
                        Resource.Failure(true, null, null)
                    }
                }
            }
        }
    }

    suspend fun logout(api: UserApi) = safeApiCall {
        api.logout()
    }
}