package com.osamaaftab.arch.di.module

import com.osamaaftab.arch.common.SchedulerProvider
import com.osamaaftab.arch.common.ToDoSchedulerProvider
import com.osamaaftab.arch.data.repository.CatsRepositoryImpl
import com.osamaaftab.arch.data.source.LocalDataSource
import com.osamaaftab.arch.data.source.RemoteDataSource
import com.osamaaftab.arch.domain.repository.CatsRepository
import org.koin.dsl.module

val RepositoryModule = module {
    single { provideNewsRepository(get(), get(), ToDoSchedulerProvider()) }
}

fun provideNewsRepository(
    tasksLocalDataSourceLocalDataSource: LocalDataSource,
    tasksRemoteDataSourceRemoteDataSource: RemoteDataSource,
    schedulerProvider: SchedulerProvider
): CatsRepository {
    return CatsRepositoryImpl(
        tasksLocalDataSourceLocalDataSource,
        tasksRemoteDataSourceRemoteDataSource,
        schedulerProvider
    )
}

