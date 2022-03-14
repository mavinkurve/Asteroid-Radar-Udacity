package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

interface NasaApi {
    @GET("/planetary/apod")
    fun getPictureOfDay(
        @Query("api_key") api_key : String
    ): Deferred<NetworkPictureOfDay>

    @GET("/neo/rest/v1/feed")
    fun getNeoWs(
        @Query("api_key") api_key: String,
        @Query("start_date") startDate: String
    ): Deferred<String>
}

/**
 * Main entry point for network access
 */
object Network {
    // Configure retrofit to parse JSON, Scalar and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(ScalarsConverterFactory.create())
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .build()
    val apiService = retrofit.create(NasaApi::class.java)
}
