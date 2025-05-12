package com.example.edusync.presentation.views.forgotPassword

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.email_text_field.EmailTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.forgotPassword.ForgotPasswordViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ForgotPasswordScreen() {
    val viewModel: ForgotPasswordViewModel = koinViewModel()
    val viewState by viewModel.viewState.observeAsState(ForgotPasswordState())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "Восстановление пароля",
                        textAlign = TextAlign.Center,
                        style = AppTypography.title.copy(fontSize = 24.sp),
                        color = AppColors.Secondary
                    )
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(32.dp))

                EmailTextField(
                    value = viewState.email,
                    onValueChange = viewModel::onEmailChanged,
                    isError = viewState.emailError != null,
                    errorMessage = viewState.emailError.orEmpty(),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Button(
            onClick = { viewModel.goToEnterCode() },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text("Далее", style = AppTypography.body1.copy(fontSize = 14.sp))
        }
    }
}