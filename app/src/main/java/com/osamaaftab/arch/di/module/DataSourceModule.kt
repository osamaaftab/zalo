package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.data.api.CatsApiService
import com.osamaaftab.arch.data.source.local.CatsDao
import com.osamaaftab.arch.data.source.local.LocalDataSourceImp
import com.osamaaftab.arch.data.source.remote.RemoteDataSourceImp
import org.koin.dsl.module

val DataSourceModule = module {
    single(override = true) { provideTasksRemoteDataSource(get()) }
    single(override = true) { provideTasksLocalDataSource(get()) }
}

fun provideTasksRemoteDataSource(taskApiService: CatsApiService): com.osamaaftab.arch.data.source.RemoteDataSource {
    return RemoteDataSourceImp(taskApiService)
}

fun provideTasksLocalDataSource(taskDao: CatsDao): com.osamaaftab.arch.data.source.LocalDataSource {
    return LocalDataSourceImp(taskDao)
}
