package com.osamaaftab.arch.di.module

import android.content.Context
import com.osamaaftab.arch.data.source.local.CatsDao
import com.osamaaftab.arch.data.source.local.ToDoDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val DaoModule = module {
    single { provideTasksDao(androidContext()) }
}

fun provideTasksDao(applicationContext: Context): CatsDao {
    return ToDoDatabase.getInstance(applicationContext).catsDao()
}


