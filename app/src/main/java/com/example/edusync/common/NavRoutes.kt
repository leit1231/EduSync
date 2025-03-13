package com.example.edusync.common

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ForgotPassword : NavRoutes("forgot_password")
    object EnterCode : NavRoutes("enter_code")
    object ChangePassword : NavRoutes("change_password")
    object InfoStudent: NavRoutes("info_student")
    object InfoTeacher: NavRoutes("info_teacher")
}