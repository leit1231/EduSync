package com.example.edusync.presentation.views.settings_screen

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.components.modal_window.DeleteAccountWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.settings.SettingsViewModel
import com.example.edusync.presentation.views.settings_screen.components.SettingsItem
import org.koin.androidx.compose.koinViewModel

@Composable
fun SettingsScreen() {

    val viewModel: SettingsViewModel = koinViewModel()
//    val notificationsEnabled by viewModel.notificationsEnabled.collectAsState()
    val isLogoutDialogVisible = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val emailSubject = stringResource(R.string.feedback_email_subject)
    val noEmailAppText = stringResource(R.string.no_email_app)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = R.drawable.ic_back),
                contentDescription = "Back",
                modifier = Modifier
                    .clickable {
                        viewModel.goBack()
                    }
                    .size(30.dp),
                tint = AppColors.Primary
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = stringResource(R.string.settings),
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
                .fillMaxWidth()
                .border(1.dp, Color.Green, RoundedCornerShape(8.dp))
        ) {
//            SettingsItem(
//                title = stringResource(R.string.notification),
//                toggleState = notificationsEnabled,
//                onToggleChange = { viewModel.toggleNotifications(it) }
//            )
//
//            HorizontalDivider(thickness = 1.dp, color = Color.Green)

            SettingsItem(
                title = stringResource(R.string.tablet_files),
                onClick = {
                    val url = "https://drive.google.com/drive/folders/1kUYiSAafghhYR0ARyXwPW1HZPpHcFIag?usp=sharing"
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            )

            HorizontalDivider(thickness = 1.dp, color = Color.Green)

            SettingsItem(
                title = stringResource(R.string.feedback),
                onClick = {
                    val email = "edusync56@gmail.com"
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:$email")
                        putExtra(Intent.EXTRA_SUBJECT, emailSubject)
                    }

                    try {
                        context.startActivity(intent)
                    } catch (e: ActivityNotFoundException) {
                        Toast.makeText(
                            context,
                            noEmailAppText,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )

            HorizontalDivider(thickness = 1.dp, color = Color.Green)

            SettingsItem(
                title = stringResource(R.string.about_app),
                onClick = {
                    viewModel.navigateToAboutAppScreen()
                }
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                isLogoutDialogVisible.value = true
            },
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent
            ),
            contentPadding = PaddingValues(horizontal = 4.dp),
            border = BorderStroke(1.dp, color = AppColors.Error),
            shape = RoundedCornerShape(100.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
                .height(40.dp)
        ) {
            Text(
                text = stringResource(R.string.delete_account),
                color = AppColors.Secondary,
                style = AppTypography.body1.copy(fontSize = 14.sp),
                maxLines = 1
            )
        }
    }
    if (isLogoutDialogVisible.value) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f))
                    .blur(15.dp)
                    .clickable { isLogoutDialogVisible.value = false }
            )
            DeleteAccountWindow(
                onClick = { viewModel.deleteAccount() },
                onDismiss = { isLogoutDialogVisible.value = false },
                modifier = Modifier
                    .align(Alignment.Center)
            )
        }
    }
}