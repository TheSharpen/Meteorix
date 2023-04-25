package com.example.meteorix

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    val api: MeteoriteApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://data.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MeteoriteApi::class.java)
    }
}