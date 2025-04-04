package com.example.edusync.presentation.viewModels.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.launch

class OnboardingViewModel(private val navigator: Navigator): ViewModel() {

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

}