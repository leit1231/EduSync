package com.example.edusync.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object OnboardingGraph: Destination

    @Serializable
    data object AuthGraph: Destination

    @Serializable
    data object OnboardingScreen: Destination

    @Serializable
    data object ForgotPasswordGraph: Destination

    @Serializable
    data object MainGraph: Destination

    @Serializable
    data object SplashScreen: Destination

    @Serializable
    data object LoginScreen: Destination

    @Serializable
    data object RegisterScreen: Destination

    @Serializable
    data object ForgotPasswordScreen: Destination

    @Serializable
    data object EnterCodeScreen: Destination

    @Serializable
    data object ChangePasswordScreen: Destination

    @Serializable
    data class InfoScreen(val email: String, val password: String, val role: Boolean): Destination

    @Serializable
    data object MainScreen: Destination

    @Serializable
    data object MaterialsScreen: Destination

    @Serializable
    data object FavoritiesScreen: Destination

    @Serializable
    data object ProfileScreen: Destination

    @Serializable
    data object SettingsScreen: Destination

    @Serializable
    data object CreateGroupScreen: Destination

    @Serializable
    data class GroupScreen(val name: String): Destination

    @Serializable
    data object SearchScreen: Destination

    @Serializable
    data object AllScheduleLayout: Destination

    @Serializable
    data object ConfirmEmailScreen: Destination

}