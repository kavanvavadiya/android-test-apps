package com.ihrsachin.sensoreventmanageger.network

import com.ihrsachin.sensoreventmanageger.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource{
    companion object{
        private  const val  BASE_URL = "http://ec2-65-0-11-224.ap-south-1.compute.amazonaws.com/auth-api/auth/"
    }

    // creating API interface with Class<Api> as parameter for blueprint of the interface

    fun<Api> buildApi(
        api: Class<Api>,
        authToken: String? = null
    ): Api {

        // logging interceptor is used to log the details of HTTP request & responses & for debugging purposes
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder()
                .addInterceptor{ chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        it.addHeader("Authorization", "Bearer $authToken")
                    }.build())
                }
                .also { client ->
                if(BuildConfig.DEBUG) {
                    val logging = HttpLoggingInterceptor()
                    logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logging)
                }
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }
}

