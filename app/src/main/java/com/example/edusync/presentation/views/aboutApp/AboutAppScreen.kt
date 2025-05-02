package com.example.edusync.presentation.views.aboutApp

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.aboutApp.AboutAppScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun AboutAppScreen() {
    val viewModel: AboutAppScreenViewModel = koinViewModel()
    val version = viewModel.appVersion.collectAsState().value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .clickable { viewModel.goBack() }
                    .size(30.dp),
                tint = AppColors.Primary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "О приложении",
                textAlign = TextAlign.Center,
                style = AppTypography.title.copy(fontSize = 24.sp),
                color = AppColors.Secondary,
                modifier = Modifier.offset(x = (-16).dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_logo_app),
                contentDescription = "App Icon",
                modifier = Modifier.size(200.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Edu Sync", style = AppTypography.body1, fontSize = 24.sp, color = AppColors.Secondary)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Версия: $version", style = AppTypography.body1, fontSize = 14.sp, color = AppColors.Secondary)
        }
    }
}