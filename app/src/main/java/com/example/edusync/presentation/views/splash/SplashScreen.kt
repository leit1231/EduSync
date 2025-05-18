package com.example.edusync.presentation.views.splash

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.splash.SplashViewMode
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen() {

    val viewModel = koinViewModel<SplashViewMode>()
    val context = LocalContext.current.applicationContext
    val encryptedPrefs = remember { EncryptedSharedPreference(context) }
    val accessToken = encryptedPrefs.getAccessToken()

    LaunchedEffect(Unit) {
        if (accessToken != null) {
            viewModel.goToMainScreen()
        } else {
            if (encryptedPrefs.isFirstLaunch()) {
                viewModel.goToOnboarding()
            } else {
                viewModel.goToLogin()
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                painter = painterResource(id = R.drawable.splash_screen_icon),
                contentDescription = null,
                modifier = Modifier.size(128.dp),
                tint = AppColors.Primary
            )
            Text(
                text = stringResource(R.string.app_name),
                style = AppTypography.body1,
                textAlign = TextAlign.Center,
                color = AppColors.Secondary,
                fontSize = 32.sp
            )
        }

        object : CountDownTimer(2000, 1000) {
            override fun onTick(millisUntilFinished: Long) {}

            override fun onFinish() {
                encryptedPrefs.setFirstLaunch(false)
            }
        }.start()
    }
}