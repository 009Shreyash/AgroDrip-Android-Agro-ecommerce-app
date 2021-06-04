package com.agrodrip.webservices

import android.content.Context
import com.agrodrip.utils.Constants
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiHandler {
    private const val HTTP_TIMEOUT: Long = 30000
    private var apiService: APIInterface? = null
    private var apiService1: APIInterface? = null
    fun getApiService(context: Context): APIInterface? {
        return if (apiService == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val client = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val newRequest = chain.request().newBuilder()
                        .build()
                    chain.proceed(newRequest)
                }.addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
            apiService = retrofit.create(APIInterface::class.java)
            apiService
        } else {
            apiService
        }
    }

    fun getApiWeatherService(): APIInterface? {
        return if (apiService1 == null) {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val gson = GsonBuilder()
                .setLenient()
                .create()
            val client = OkHttpClient.Builder()
                .addInterceptor { chain: Interceptor.Chain ->
                    val newRequest = chain.request().newBuilder()
                        .build()
                    chain.proceed(newRequest)
                }.addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder()
                .baseUrl("http://dataservice.accuweather.com")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build()
            apiService1 = retrofit.create(APIInterface::class.java)
            apiService1
        } else {
            apiService1
        }
    }
}