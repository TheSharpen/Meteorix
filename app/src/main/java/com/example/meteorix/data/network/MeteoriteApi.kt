package com.example.meteorix.data.network

import com.example.meteorix.data.model.Meteorite
import retrofit2.Response
import retrofit2.http.GET

interface MeteoriteApi {

    @GET("/resource/gh4g-9sfh.json")
    suspend fun getMeteorites(): Response<List<Meteorite>>

}