package com.example.edusync.di

import com.example.edusync.presentation.viewModels.changePasswordAfterForgot.ChangePasswordAfterForgotViewModel
import com.example.edusync.presentation.viewModels.enterCode.EnterCodeViewModel
import com.example.edusync.presentation.viewModels.favorite.FavoritesViewModel
import com.example.edusync.presentation.viewModels.forgotPassword.ForgotPasswordViewModel
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel
import com.example.edusync.presentation.viewModels.login.LoginViewModel
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.viewModels.materials.CreateGroupViewModel
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel
import com.example.edusync.presentation.viewModels.profile.ProfileScreenViewModel
import com.example.edusync.presentation.viewModels.register.RegisterViewModel
import com.example.edusync.presentation.viewModels.search.SearchViewModel
import com.example.edusync.presentation.viewModels.settings.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel() }
    viewModel { RegisterViewModel() }
    viewModel { ForgotPasswordViewModel() }
    viewModel { EnterCodeViewModel() }
    viewModel { ChangePasswordAfterForgotViewModel() }
    viewModel { InfoStudentViewModel() }
    viewModel { MainScreenViewModel() }
    viewModel { ProfileScreenViewModel() }
    viewModel { SettingsViewModel() }
    viewModel { FavoritesViewModel() }
    viewModel { MaterialsScreenViewModel() }
    viewModel { CreateGroupViewModel() }
    viewModel { SearchViewModel() }
}
