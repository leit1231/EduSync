package com.example.edusync.presentation.views.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.theme.ui.EduSyncTheme

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            EduSyncTheme {
                val navController = rememberNavController()
                Navigator(navController = navController)
            }
        }
    }
}
