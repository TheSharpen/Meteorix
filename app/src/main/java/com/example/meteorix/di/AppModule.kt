package com.example.meteorix.di

import com.example.meteorix.data.network.MeteoriteApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMeteoriteApi(): MeteoriteApi {
        return Retrofit.Builder()
            .baseUrl("https://data.nasa.gov/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MeteoriteApi::class.java)
    }

}