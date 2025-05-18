package com.example.edusync.presentation.views.main

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import android.Manifest
import androidx.compose.runtime.remember
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.presentation.navigation.Navigator
import com.example.edusync.presentation.theme.ui.EduSyncTheme
import org.koin.compose.koinInject

val LocalNavigator = compositionLocalOf<Navigator> { error("No Navigator provided") }

class MainActivity : ComponentActivity() {
    private lateinit var encryptedPrefs: EncryptedSharedPreference

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        encryptedPrefs = EncryptedSharedPreference(this)

        if (encryptedPrefs.isFirstLaunch()) {
            requestFilePermissions()
        }

        val currentIntent = intent

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            EduSyncTheme {
                val navController = rememberNavController()
                val navigator = koinInject<Navigator>()

                val inviteCode = remember(currentIntent) {
                    currentIntent?.data?.lastPathSegment
                }

                CompositionLocalProvider(LocalNavigator provides navigator) {
                    Navigator(
                        navController = navController,
                        navigator = navigator,
                        inviteCode = inviteCode
                    )
                }
            }
        }
    }

    private fun requestFilePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                ), 1001
            )
        } else {
            requestPermissions(
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1001
            )
        }
    }
}