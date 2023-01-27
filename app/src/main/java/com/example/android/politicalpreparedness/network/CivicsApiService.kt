package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.network.jsonadapter.DateAdapter
import com.example.android.politicalpreparedness.network.jsonadapter.ElectionAdapter
import com.example.android.politicalpreparedness.network.models.ElectionResponse
import com.example.android.politicalpreparedness.network.models.RepresentativeResponse
import com.example.android.politicalpreparedness.network.models.VoterInfoResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://www.googleapis.com/civicinfo/v2/"

/**
 * The converter for parsing json response requires two custom adapter
 * ElectionAdapter and DateAdapter for mapping string to associate objects
 * 1. Division -> ocdDivisionId
 * 2. Date -> electionDay
 */
private val moshi = Moshi.Builder()
    .add(ElectionAdapter())
    .add(KotlinJsonAdapterFactory())
    .add(DateAdapter())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .client(CivicsHttpClient.getClient())
    .baseUrl(BASE_URL)
    .build()

/**
 *  Documentation for the Google Civics API Service can be found at https://developers.google.com/civic-information/docs/v2
 */

interface CivicsApiService {
    /**
     * get Elections from Google Civic API
     */
    @GET("elections")
    suspend fun getElections(): ElectionResponse

    /**
     * get VoterInfo from Google Civic API
     */
    @GET("voterinfo")
    suspend fun getVoterInfo(
        @Query("address") address: String,
        @Query("electionId") electionId: Long
    ): VoterInfoResponse

    /**
     * get representatives from Google Civic API
     */
    @GET("representatives")
    suspend fun getRepresentatives(@Query("address") address: String): RepresentativeResponse
}

object CivicsApi {
    val retrofitService: CivicsApiService by lazy {
        retrofit.create(CivicsApiService::class.java)
    }
}