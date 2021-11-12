package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.data.api.CatsApiService
import org.koin.dsl.module
import retrofit2.Retrofit

val ApiServicesModule = module {
    single { provideUserService(get()) }
}

fun provideUserService(retrofit: Retrofit): CatsApiService {
    return retrofit.create(CatsApiService::class.java)
}