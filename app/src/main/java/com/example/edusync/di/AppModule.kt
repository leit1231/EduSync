package com.example.edusync.di

import com.example.edusync.presentation.navigation.DefaultNavigator
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import org.koin.dsl.module

val appModule = module {
    single<Navigator> {
        DefaultNavigator(
            startDestination = Destination.OnboardingGraph
        )
    }
}