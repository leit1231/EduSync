package com.example.edusync.presentation.navigation

import android.net.Uri
import android.os.Build
import android.util.Log
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.toRoute
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.changePasswordAfterForgot.ChangePasswordAfterForgotViewModel
import com.example.edusync.presentation.viewModels.confirmEmail.ConfirmEmailViewModel
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.views.aboutApp.AboutAppScreen
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
import com.example.edusync.presentation.views.pdfScreen.PdfScreen
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
            Log.d("MainScreen", "$listScreen , ${currentDestination.value?.destination?.route?.substringBefore("?")}")
            val currentRoute = currentDestination.value?.destination?.route?.substringBefore("?")
            if (listScreen.contains(currentRoute)) {
                NavigationMenu(
                    navigator = navigator,
                    currentRoute = currentRoute
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
                        val (email, password) = it.toRoute<Destination.ConfirmEmailScreen>()
                        val viewModel: ConfirmEmailViewModel = koinViewModel(parameters = { parametersOf(email, password) })
                        ConfirmEmailScreen(viewModel)
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
                        val (code) = it.toRoute<Destination.ChangePasswordScreen>()
                        val viewModel: ChangePasswordAfterForgotViewModel = koinViewModel(parameters = { parametersOf(code) })
                        ChangePasswordAfterForgotScreen(viewModel)
                    }
                }

                navigation<Destination.MainGraph>(
                    startDestination = Destination.MainScreen()
                ) {
                    composable<Destination.MainScreen> {
                        val args = it.toRoute<Destination.MainScreen>()
                        MainScreen(args.groupId, args.groupName, args.teacherId, args.teacherName)
                    }
                    composable<Destination.MaterialsScreen> {
                        MaterialsScreen()
                    }
                    composable<Destination.FavoritiesScreen> {
                        FavoritiesScreen()
                    }
                    composable<Destination.PdfScreenDestination> {
                        val args = it.toRoute<Destination.PdfScreenDestination>()
                        val uri = Uri.parse(args.uri)
                        PdfScreen(uri = uri) {
                            navController.popBackStack()
                        }
                    }
                    composable<Destination.ProfileScreen> {
                        ProfileScreen()
                    }
                    composable<Destination.SettingsScreen> {
                        SettingsScreen()
                    }
                    composable<Destination.AboutAppScreen>{
                        AboutAppScreen()
                    }
                    composable<Destination.CreateGroupScreen> {
                        CreateGroupScreen()
                    }
                    composable<Destination.GroupScreen> {
                        val groupName = it.toRoute<Destination.GroupScreen>()
                        val groupId = it.toRoute<Destination.GroupScreen>()
                        GroupScreen(groupId, groupName)
                    }
                    composable<Destination.SearchScreen> { backStackEntry ->
                        val (isTeacherSearch, institutionId) = backStackEntry.toRoute<Destination.SearchScreen>()
                        val viewModel: SearchViewModel = koinViewModel()
                        val currentBackStackEntry = navController.currentBackStackEntryAsState()
                        val parentEntry = currentBackStackEntry.value?.let { entry ->
                            if (entry.destination.route == Destination.MainScreen::class.qualifiedName) {
                                entry
                            } else {
                                null
                            }
                        } ?: run {
                            navController.getBackStackEntry(Destination.MainScreen::class.qualifiedName!!)
                        }
                        viewModel.setInitialData(isTeacherSearch, institutionId)

                        SearchScreen(
                            viewModel = viewModel,
                            onGroupSelected = { group ->
                                parentEntry.savedStateHandle["selected_group_id"] = group.id
                                Log.d("MainScreen", "Selected group ID: ${group.id}")
                                parentEntry.savedStateHandle["selected_group_name"] = group.name
                                Log.d("MainScreen", "Selected group ID: ${group.name}")
                                navController.navigate(Destination.MainScreen(groupId = group.id, groupName = group.name)){
                                    popUpTo(0){
                                        inclusive = true
                                    }
                                }

                            },
                            onTeacherSelected = { teacher ->
                                parentEntry.savedStateHandle["selected_teacher_id"] = teacher.id
                                Log.d("MainScreen", "Selected group ID: ${teacher.id}")
                                parentEntry.savedStateHandle["selected_teacher_name"] = teacher.initials
                                Log.d("MainScreen", "Selected group ID: ${teacher.initials}")
                                navController.navigate(Destination.MainScreen(teacherId = teacher.id, teacherName = teacher.initials)){
                                    popUpTo(0){
                                        inclusive = true
                                    }
                                }
                            }
                        )
                    }
                    composable<Destination.AllScheduleLayout> {
                        AllWeekScheduleLayout()
                    }
                }
            }
        }
    )
}