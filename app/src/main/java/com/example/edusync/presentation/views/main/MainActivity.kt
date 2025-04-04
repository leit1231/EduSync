package com.example.edusync.presentation.views.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.compose.rememberNavController
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.theme.ui.EduSyncTheme
import org.koin.compose.koinInject

val LocalNavigator = compositionLocalOf<Navigator> { error("No Navigator provided") }

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EduSyncTheme {
                val navController = rememberNavController()
                val navigator = koinInject<Navigator>()
                CompositionLocalProvider(
                    LocalNavigator provides navigator
                ) { Navigator(navController = navController, navigator) }
            }
        }
    }
}
