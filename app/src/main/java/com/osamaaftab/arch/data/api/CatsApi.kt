package com.osamaaftab.arch.data.api

import androidx.annotation.VisibleForTesting
import com.google.gson.Gson
import com.osamaaftab.arch.BuildConfig
import com.osamaaftab.arch.data.api.CatsApiService.Config
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class CatsApi {

    var service: CatsApiService

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) Level.BASIC else Level.NONE
        })
        .connectTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(CONNECTION_TIMEOUT, TimeUnit.SECONDS)
        .build()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Config.BASE_URL)
            .client(okHttpClient)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .build()
        service = retrofit.create(CatsApiService::class.java)
    }

    companion object {
        private const val CONNECTION_TIMEOUT = 30L // in seconds

        private var INSTANCE: CatsApi? = null

        fun getInstance(
        ): CatsApi = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CatsApi().also { INSTANCE = it }
        }

        @VisibleForTesting
        fun destroy() {
            INSTANCE = null
        }
    }
}