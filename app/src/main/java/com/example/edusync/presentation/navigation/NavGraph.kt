package com.example.edusync.presentation.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.views.changePasswordAfterForgot.ChangePasswordAfterForgotScreen
import com.example.edusync.presentation.views.confirmEmail.ConfirmEmailScreen
import com.example.edusync.presentation.views.enterCode.EnterCodeScreen
import com.example.edusync.presentation.views.favorities.FavoritiesScreen
import com.example.edusync.presentation.views.forgotPassword.ForgotPasswordScreen
import com.example.edusync.presentation.views.group.GroupScreen
import com.example.edusync.presentation.views.infoScreen.InfoScreen
import com.example.edusync.presentation.views.login.Login
import com.example.edusync.presentation.views.main.mainScreen.MainScreen
import com.example.edusync.presentation.views.main.shedule.AllWeekScheduleLayout
import com.example.edusync.presentation.views.materials.MaterialsScreen
import com.example.edusync.presentation.views.materials.group.CreateGroupScreen
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu
import com.example.edusync.presentation.views.onboarding.onboarding_navigation_pager.OnboardingPagerScreen
import com.example.edusync.presentation.views.profile.ProfileScreen
import com.example.edusync.presentation.views.register.RegisterScreen
import com.example.edusync.presentation.views.search_screen.SearchScreen
import com.example.edusync.presentation.views.settings_screen.SettingsScreen
import com.example.edusync.presentation.views.splash.SplashScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigator(navController: NavHostController, navigator: Navigator) {

    val currentDestination = navController.currentBackStackEntryAsState()

    ObserveAsEvents(flow = navigator.navigationAction) {action ->
        when(action){
            is NavigationAction.Navigate -> navController.navigate(
                action.destination
            ){
                action.navOptions(this
                )
            }
            NavigationAction.NavigateUp -> navController.navigateUp()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = {

            val listScreen = listOf(Destination.MainScreen::class.qualifiedName, Destination.MaterialsScreen::class.qualifiedName, Destination.FavoritiesScreen::class.qualifiedName, Destination.ProfileScreen::class.qualifiedName)

            if (listScreen.contains(currentDestination.value?.destination?.route)) {
                NavigationMenu(
                    navigator = navigator,
                    currentRoute = currentDestination.value?.destination?.route
                )
            }else{
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = AppColors.Background)
                )
            }
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.systemBars),
        content = { paddingValues ->

            NavHost(modifier = Modifier.padding(paddingValues), navController = navController, startDestination = navigator.startDestination) {

                navigation<Destination.OnboardingGraph>(
                    startDestination = Destination.SplashScreen
                ) {
                    composable<Destination.SplashScreen> {
                        SplashScreen()
                    }
                    composable<Destination.OnboardingScreen> {
                        OnboardingPagerScreen()
                    }
                }

                navigation<Destination.AuthGraph>(
                    startDestination = Destination.LoginScreen
                ) {
                    composable<Destination.LoginScreen> {
                        Login()
                    }
                    composable<Destination.RegisterScreen> {
                        RegisterScreen()
                    }
                    composable<Destination.InfoScreen> {
                        val (email, password, role) = it.toRoute<Destination.InfoScreen>()
                        val viewModel: InfoStudentViewModel = koinViewModel(parameters = { parametersOf(email, password, role) } )
                        InfoScreen(viewModel)
                    }
                    composable<Destination.ConfirmEmailScreen> {
                        ConfirmEmailScreen()
                    }
                }

                navigation<Destination.ForgotPasswordGraph>(
                    startDestination = Destination.ForgotPasswordScreen
                ) {
                    composable<Destination.ForgotPasswordScreen> {
                        ForgotPasswordScreen()
                    }
                    composable<Destination.EnterCodeScreen> {
                        EnterCodeScreen()
                    }
                    composable<Destination.ChangePasswordScreen> {
                        ChangePasswordAfterForgotScreen()
                    }
                }

                navigation<Destination.MainGraph>(
                    startDestination = Destination.MainScreen
                ) {
                    composable<Destination.MainScreen> {
                        MainScreen()
                    }
                    composable<Destination.MaterialsScreen> {
                        MaterialsScreen()
                    }
                    composable<Destination.FavoritiesScreen> {
                        FavoritiesScreen()
                    }
                    composable<Destination.ProfileScreen> {
                        ProfileScreen()
                    }
                    composable<Destination.SettingsScreen> {
                        SettingsScreen()
                    }
                    composable<Destination.CreateGroupScreen> {
                        CreateGroupScreen()
                    }
                    composable<Destination.GroupScreen> {
                        val groupName = it.toRoute<Destination.GroupScreen>()
                        GroupScreen(groupName)
                    }
                    composable<Destination.SearchScreen> {
                        val (isTeacherSearch, institutionId) = it.toRoute<Destination.SearchScreen>()
                        val viewModel: SearchViewModel = koinViewModel()
                        viewModel.setInitialData(isTeacherSearch, institutionId)
                        SearchScreen(viewModel,
                            onGroupSelected = { groupName ->
                                navController.previousBackStackEntry?.savedStateHandle?.set("selected_group", groupName)
                            })
                    }
                    composable<Destination.AllScheduleLayout> {
                        AllWeekScheduleLayout()
                    }
                }
            }
        }
    )
}