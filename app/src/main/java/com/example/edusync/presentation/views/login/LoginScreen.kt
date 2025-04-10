package com.example.edusync.presentation.views.login

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.components.custom_text_field.email_text_field.EmailTextField
import com.example.edusync.presentation.components.custom_text_field.password_text_field.PasswordTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.login.LoginViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun Login() {

    val viewModel: LoginViewModel = koinViewModel()
    val state by viewModel.uiState.collectAsState()

    val isFormValid = state.email.isNotBlank() &&
            state.password.isNotBlank()

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(16.dp)
        ) {

            Text(
                text = "Вход",
                style = AppTypography.title.copy(fontSize = 24.sp),
                color = AppColors.Secondary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(32.dp))

            EmailTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                isError = state.emailError != null,
                errorMessage = state.emailError
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                isError = state.passwordError != null,
                errorMessage = state.passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Забыли пароль?",
                    modifier = Modifier
                        .clickable { viewModel.goToForgotPassword() },
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = AppColors.Primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = { viewModel.onLoginClicked()},
                enabled = isFormValid && !state.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary
                ),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                if (state.isLoading) {
                    Text(
                        "Загрузка...",
                        color = AppColors.Background,
                        style = AppTypography.title.copy(fontSize = 14.sp),
                    )
                } else {
                    Text(
                        "Войти",
                        color = AppColors.Background,
                        style = AppTypography.title.copy(fontSize = 14.sp),
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Нет аккаунта?",
                style = AppTypography.body1.copy(fontSize = 16.sp),
                color = AppColors.Secondary
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.goToRegisterScreen()
                },
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 4.dp),
                border = BorderStroke(1.dp, color = AppColors.Primary),
                shape = RoundedCornerShape(100.dp),
                modifier = Modifier.size(180.dp, 40.dp)
            ) {
                Text(
                    text = "Зарегистрироваться",
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            val annotatedString = buildAnnotatedString {
                withStyle(style = SpanStyle(color = Color.White, fontSize = 14.sp)) {
                    append("При регистрации и входе\nвы соглашаетесь с ")

                    pushStringAnnotation(tag = "URL", annotation = "https://gitlab.serega-pirat.ru/")
                    withStyle(style = SpanStyle(color = Color.Red)) {
                        append("политикой конфиденциальности")
                    }
                    pop()
                }
            }

            val context = LocalContext.current
            val textLayoutResult = remember { mutableStateOf<TextLayoutResult?>(null) }

            Text(
                text = annotatedString,
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures { offset ->
                            textLayoutResult.value?.let { layoutResult ->
                                val textOffset = layoutResult.getOffsetForPosition(offset)
                                annotatedString.getStringAnnotations(textOffset, textOffset)
                                    .firstOrNull { it.tag == "URL" }?.let { annotation ->
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
                                        context.startActivity(intent)
                                    }
                            }
                        }
                    },
                onTextLayout = { textLayoutResult.value = it },
                softWrap = false,
                overflow = TextOverflow.Visible
            )
        }
    }
}