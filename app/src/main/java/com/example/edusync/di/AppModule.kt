package com.example.edusync.di

import androidx.room.Room
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.local.entities.AppDatabase
import com.example.edusync.presentation.navigation.DefaultNavigator
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val appModule = module {
    single<Navigator> {
        DefaultNavigator(
            startDestination = Destination.OnboardingGraph
        )
    }
    single { EncryptedSharedPreference(androidContext()) }
    single {
        Room.databaseBuilder(
                androidContext(),
                AppDatabase::class.java,
                "edusync.db"
            ).fallbackToDestructiveMigration(true)
            .build()
    }
    single { get<AppDatabase>().instituteDao() }
    single { get<AppDatabase>().groupDao() }
}