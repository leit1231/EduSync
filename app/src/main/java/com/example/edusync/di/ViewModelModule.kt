package com.example.edusync.di

import com.example.edusync.presentation.viewModels.changePasswordAfterForgot.ChangePasswordAfterForgotViewModel
import com.example.edusync.presentation.viewModels.enterCode.EnterCodeViewModel
import com.example.edusync.presentation.viewModels.forgotPassword.ForgotPasswordViewModel
import com.example.edusync.presentation.viewModels.login.LoginViewModel
import com.example.edusync.presentation.viewModels.register.RegisterViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { LoginViewModel() }
    viewModel { RegisterViewModel() }
    viewModel { ForgotPasswordViewModel() }
    viewModel { EnterCodeViewModel() }
    viewModel { ChangePasswordAfterForgotViewModel() }
}
