package com.example.edusync.di

import com.example.edusync.data.local.EncryptedSharedPreference
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
}