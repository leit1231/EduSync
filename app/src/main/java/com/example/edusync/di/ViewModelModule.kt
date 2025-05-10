package com.example.edusync.di

import com.example.edusync.presentation.viewModels.aboutApp.AboutAppScreenViewModel
import com.example.edusync.presentation.viewModels.changePasswordAfterForgot.ChangePasswordAfterForgotViewModel
import com.example.edusync.presentation.viewModels.confirmEmail.ConfirmEmailViewModel
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
    viewModel { ConfirmEmailViewModel(get()) }
    viewModel { SplashViewMode(get()) }
    viewModel { OnboardingViewModel(get()) }
    viewModel { LoginViewModel(get(), get(), get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel { ForgotPasswordViewModel(get()) }
    viewModel { EnterCodeViewModel(get()) }
    viewModel { ChangePasswordAfterForgotViewModel(get()) }
    viewModel {
        parameters ->
        InfoStudentViewModel(get(), get(), get(), email = parameters.get(), password = parameters.get(), role = parameters.get(), get()
        )
    }
    viewModel { MainScreenViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { ProfileScreenViewModel(get(), get(), get(), get(), get(), get()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { FavoritesViewModel(get(), get(), get(), get()) }
    viewModel { MaterialsScreenViewModel(get(), get(), get(), get(), get()) }
    viewModel { CreateGroupViewModel(get(), get(), get(), get(), get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { GroupViewModel(
        get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()
    )}
    viewModel { AboutAppScreenViewModel(get(), get()) }
}