package com.osamaaftab.arch

import android.app.Application
import android.os.StrictMode
import com.osamaaftab.arch.di.module.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            initStrictMode()
        }
        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(
                listOf(
                    ActivityModule,
                    UseCaseModule,
                    DaoModule,
                    DataSourceModule,
                    NetWorkModule,
                    RepositoryModule,
                    ApiServicesModule
                )
            )
        }
    }

    private fun initStrictMode() {
        val threadPolicy = StrictMode.ThreadPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setThreadPolicy(threadPolicy)

        val vmPolicy = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()
            .build()
        StrictMode.setVmPolicy(vmPolicy)
    }
}