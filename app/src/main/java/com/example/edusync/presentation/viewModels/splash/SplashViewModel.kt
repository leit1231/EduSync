package com.example.edusync.presentation.viewModels.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.launch

class SplashViewMode(private val navigator: Navigator): ViewModel() {

    fun goToOnboarding(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.OnboardingScreen
            )
        }
    }

    fun goToLogin(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AuthGraph,
                navOptions = {
                    popUpTo(Destination.OnboardingGraph){
                        inclusive = true
                    }
                }
            )
        }
    }

    fun goToMainScreen(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.MainGraph,
                navOptions = {
                    popUpTo(Destination.OnboardingGraph){
                        inclusive = true
                    }
                }
            )
        }
    }
}