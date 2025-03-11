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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.common.NavRoutes
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun SplashScreen(navController: NavController) {
    val context = LocalContext.current

    val encryptedPrefs = remember { EncryptedSharedPreference(context) }

    val isFirstLaunch by remember {
        mutableStateOf(encryptedPrefs.isFirstLaunch())
    }

    LaunchedEffect(isFirstLaunch) {
        if (isFirstLaunch) {
            navController.navigate(NavRoutes.Onboarding.route)
        } else {
            navController.navigate(NavRoutes.Login.route)
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
                text = "Edu Sync",
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