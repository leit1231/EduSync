package com.example.edusync.common

sealed class NavRoutes(val route: String) {
    object Splash : NavRoutes("splash")
    object Onboarding : NavRoutes("onboarding")
    object Login : NavRoutes("login")
    object Register : NavRoutes("register")
    object ForgotPassword : NavRoutes("forgot_password")
    object EnterCode : NavRoutes("enter_code")
    object ChangePassword : NavRoutes("change_password")
    object InfoScreen: NavRoutes("info_student")
    object MainScreen: NavRoutes("main_screen_student")
    object MaterialsScreen: NavRoutes("materials_screen")
    object FavoritiesScreen: NavRoutes("favorities_screen")
    object ProfileScreen: NavRoutes("profile_screen")
    object SettingsScreen: NavRoutes("settings_screen")
    object CreateGroupScreen: NavRoutes("create_group_screen")
    object GroupScreen : NavRoutes("group_screen/{subjectName}")
    object SearchScreen: NavRoutes("search_screen")
    object AllScheduleLayout: NavRoutes("all_schedule_layout")
}