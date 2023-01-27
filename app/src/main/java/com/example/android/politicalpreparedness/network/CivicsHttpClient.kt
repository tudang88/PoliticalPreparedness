package com.example.android.politicalpreparedness.network

import com.example.android.politicalpreparedness.BuildConfig
import okhttp3.OkHttpClient
import timber.log.Timber

class CivicsHttpClient: OkHttpClient() {

    companion object {

        const val API_KEY = BuildConfig.GOOGLE_CIVIC_API_KEY

        fun getClient(): OkHttpClient {
            Timber.d("get OkHttpClient")
            return Builder()
                    .addInterceptor { chain ->
                        val original = chain.request()
                        val url = original
                                .url()
                                .newBuilder()
                                .addQueryParameter("key", API_KEY)
                                .build()
                        val request = original
                                .newBuilder()
                                .url(url)
                                .build()
                        Timber.tag("CivicsHttpClient").d("Request:$request")
                        chain.proceed(request)
                    }
                    .build()
        }

    }

}