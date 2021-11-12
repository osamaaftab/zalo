package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.BuildConfig
import com.osamaaftab.arch.data.api.CatsApiService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

val NetWorkModule = module {

    single { providesRetrofit(get()) }
    single { providesOkHttpClient() }
    single { createMoshiConverterFactory() }

    single { createMoshi() }
}


fun providesRetrofit(
        okHttpClient: OkHttpClient
): Retrofit {
    return Retrofit.Builder()
            .baseUrl(CatsApiService.Config.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create()).build()

}

fun createMoshi(): Moshi {
    return Moshi.Builder().build()
}

fun createMoshiConverterFactory(): MoshiConverterFactory {
    return MoshiConverterFactory.create()
}

fun providesOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
            .connectTimeout(30L, TimeUnit.SECONDS)
            .readTimeout(30L, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
            }).build()
}