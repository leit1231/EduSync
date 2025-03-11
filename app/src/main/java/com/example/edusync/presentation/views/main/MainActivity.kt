package com.example.edusync.presentation.views.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.edusync.di.viewModelModule
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.theme.ui.EduSyncTheme
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext.startKoin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(listOf(viewModelModule))
        }
        setContent {
            EduSyncTheme {
                val navController = rememberNavController()
                Navigator(navController = navController)
            }
        }
    }
}
