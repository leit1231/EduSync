package com.example.edusync.presentation.views.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu

@Composable
fun MaterialsScreen(navController: NavHostController){
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: "materials_screen"
    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = {
            NavigationMenu(navController, currentRoute)
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                Text("Test")
            }
        })
}