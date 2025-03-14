package com.example.edusync.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.views.changePasswordAfterForgot.ChangePasswordAfterForgotScreen
import com.example.edusync.presentation.views.enterCode.EnterCodeScreen
import com.example.edusync.presentation.views.forgotPassword.ForgotPasswordScreen
import com.example.edusync.presentation.views.infoScreen.InfoStudentScreen
import com.example.edusync.presentation.views.login.Login
import com.example.edusync.presentation.views.onboarding.onboarding_navigation_pager.OnboardingPagerScreen
import com.example.edusync.presentation.views.register.RegisterScreen
import com.example.edusync.presentation.views.splash.SplashScreen

@RequiresApi(Build.VERSION_CODES.O)
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
            Login(navController)
        }
        composable(NavRoutes.Register.route) {
            RegisterScreen(navController)
        }
        composable(NavRoutes.ForgotPassword.route) {
            ForgotPasswordScreen(navController)
        }
        composable(NavRoutes.EnterCode.route) {
            EnterCodeScreen(navController)
        }
        composable(NavRoutes.ChangePassword.route) {
            ChangePasswordAfterForgotScreen(navController)
        }
        composable(NavRoutes.InfoScreen.route) {
            InfoStudentScreen(navController)
        }
    }
}