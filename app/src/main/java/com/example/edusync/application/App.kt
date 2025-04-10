package com.example.edusync.application

import android.app.Application
import com.example.edusync.di.appModule
import com.example.edusync.di.networkModule
import com.example.edusync.di.repositoryModule
import com.example.edusync.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class App: Application(){
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@App)
            modules(
                appModule,
                viewModelModule,
                networkModule,
                repositoryModule
            )
        }
    }
}