package com.example.edusync.presentation.viewModels.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.common.Resource
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.domain.use_case.account.DeleteAccountUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val navigator: Navigator,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val pref: EncryptedSharedPreference
) : ViewModel()
{
    private val _notificationsEnabled = MutableStateFlow(true)
    val notificationsEnabled: StateFlow<Boolean> = _notificationsEnabled
    private val refreshToken = pref.getRefreshToken()

    fun toggleNotifications(enabled: Boolean) {
        _notificationsEnabled.value = enabled
    }

    fun deleteAccount() {
        viewModelScope.launch {
            val cleanToken = refreshToken?.removePrefix("Bearer ")?.trim()

            if (!cleanToken.isNullOrBlank()) {
                deleteAccountUseCase(cleanToken).collect { result ->
                    if (result is Resource.Success) {
                        navigator.navigate(
                            destination = Destination.AuthGraph,
                            navOptions = {
                                popUpTo(Destination.MainGraph) { inclusive = true }
                            }
                        )
                    }
                }
            }
        }
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
