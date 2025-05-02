package com.example.edusync.presentation.viewModels.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(private val navigator: Navigator) : ViewModel() {
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun navigateToLogin(){
        viewModelScope.launch {
            navigator.navigate(
                destination = Destination.AuthGraph,
                navOptions = {
                    popUpTo(Destination.MainGraph){
                        inclusive = true
                    }
                }
            )
        }
    }

    fun navigateToAboutAppScreen(){
        viewModelScope.launch {
            navigator.navigate(Destination.AboutAppScreen)
        }
    }

    fun goBack(){
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}
