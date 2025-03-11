package com.example.edusync.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.views.login.Login
import com.example.edusync.presentation.views.onboarding.onboarding_navigation_pager.OnboardingPagerScreen
import com.example.edusync.presentation.views.splash.SplashScreen

@Composable
fun Navigator(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NavRoutes.Splash.route) {
        composable(NavRoutes.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(NavRoutes.Onboarding.route) {
            OnboardingPagerScreen(navController)
        }
        composable(NavRoutes.Login.route) {
            Login()
        }
    }
}