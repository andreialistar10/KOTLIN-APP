package com.andrei.entities.core

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api{

    const val HOST_IP = "192.168.100.3"

    private const val HOST_URL = "http://$HOST_IP"

    val tokenInterceptor = TokenInterceptor()

    private val client: OkHttpClient = OkHttpClient.Builder().apply{
        this.addInterceptor(tokenInterceptor)
    }.build()

    private var gson = GsonBuilder()
        .setLenient()
        .create()

    val retrofit = Retrofit.Builder()
        .baseUrl("$HOST_URL:8080/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()
}