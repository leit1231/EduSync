package com.example.edusync.application

import android.app.Application
import com.example.edusync.di.appModule
import com.example.edusync.di.networkModule
import com.example.edusync.di.repositoryModule
import com.example.edusync.di.viewModelModule
import com.example.edusync.domain.repository.group.GroupRepository
import com.example.edusync.domain.repository.institution.InstituteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
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
        val initializer = AppInitializer(
            get<InstituteRepository>(),
            get<GroupRepository>()
        )
        CoroutineScope(Dispatchers.IO).launch {
            initializer.initialize()
        }
    }
}