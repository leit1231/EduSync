package com.example.edusync.presentation.viewModels.aboutApp

import android.app.Application
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AboutAppScreenViewModel(
    application: Application,
    private val navigator: Navigator
) : AndroidViewModel(application) {

    private val _appVersion = MutableStateFlow("N/A")
    val appVersion: StateFlow<String> = _appVersion

    init {
        val context = application.applicationContext
        _appVersion.value = try {
            val info = context.packageManager.getPackageInfo(context.packageName, 0)
            info.versionName ?: "N/A"
        } catch (e: PackageManager.NameNotFoundException) {
            "N/A"
        }
    }

    fun goBack() {
        viewModelScope.launch {
            navigator.navigateUp()
        }
    }
}