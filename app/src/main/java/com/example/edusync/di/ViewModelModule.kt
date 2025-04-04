package com.example.edusync.di

import com.example.edusync.presentation.viewModels.changePasswordAfterForgot.ChangePasswordAfterForgotViewModel
import com.example.edusync.presentation.viewModels.enterCode.EnterCodeViewModel
import com.example.edusync.presentation.viewModels.favorite.FavoritesViewModel
import com.example.edusync.presentation.viewModels.forgotPassword.ForgotPasswordViewModel
import com.example.edusync.presentation.viewModels.group.GroupViewModel
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel
import com.example.edusync.presentation.viewModels.login.LoginViewModel
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.viewModels.materials.CreateGroupViewModel
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel
import com.example.edusync.presentation.viewModels.onboarding.OnboardingViewModel
import com.example.edusync.presentation.viewModels.profile.ProfileScreenViewModel
import com.example.edusync.presentation.viewModels.register.RegisterViewModel
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.viewModels.settings.SettingsViewModel
import com.example.edusync.presentation.viewModels.splash.SplashViewMode
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { SplashViewMode(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { EnterCodeViewModel(get()) }
    viewModel { ChangePasswordAfterForgotViewModel(get()) }
    viewModel { InfoStudentViewModel(get()) }
    viewModel { MainScreenViewModel(get()) }
    viewModel { ProfileScreenViewModel(get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { FavoritesViewModel(get()) }
    viewModel { MaterialsScreenViewModel(get()) }
    viewModel { CreateGroupViewModel(get()) }
    viewModel { SearchViewModel() }
    viewModel { GroupViewModel(get()) }
}
