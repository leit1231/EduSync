package com.example.edusync.presentation.views.register

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.components.custom_text_field.email_text_field.EmailTextField
import com.example.edusync.presentation.components.custom_text_field.password_text_field.PasswordTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.register.RegisterViewModel
import com.example.edusync.presentation.views.register.student_teacher_button.RoleSelectionButtons
import org.koin.androidx.compose.koinViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegisterScreen() {
    val viewModel: RegisterViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    var isTeacherSelected by remember { mutableStateOf(false) }

    val isFormValid = state.email.isNotBlank() &&
            state.password.isNotBlank() &&
            state.passwordConfirmation.isNotBlank() &&
            state.emailError == null &&
            state.passwordError == null &&
            state.passwordConfirmationError == null

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(R.string.registration),
            style = AppTypography.body1.copy(fontSize = 24.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        EmailTextField(
            value = state.email,
            onValueChange = viewModel::setEmail,
            isError = state.emailError != null,
            errorMessage = state.emailError
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            value = state.password,
            onValueChange = viewModel::setPassword,
            isError = state.passwordError != null,
            errorMessage = state.passwordError
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordTextField(
            label = stringResource(R.string.confirm_password),
            value = state.passwordConfirmation,
            onValueChange = viewModel::setConfirmPassword,
            isError = state.passwordConfirmationError != null,
            errorMessage = state.passwordConfirmationError
        )

        Spacer(modifier = Modifier.height(16.dp))

        RoleSelectionButtons(
            selected = isTeacherSelected,
            onStudentClick = {
                isTeacherSelected = false
                viewModel.setRole(false)
            },
            onTeacherClick = {
                isTeacherSelected = true
                viewModel.setRole(true)
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isTeacherSelected) {
            Text(
                text = stringResource(R.string.coop_email),
                color = AppColors.Secondary,
                style = AppTypography.body1.copy(fontSize = 12.sp),
                modifier = Modifier
                    .fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { viewModel.register() },
            enabled = isFormValid,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            )
        ) {
            Text(
                text = stringResource(R.string.registration),
                color = AppColors.Background,
                style = AppTypography.title.copy(fontSize = 14.sp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = stringResource(R.string.have_account),
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.goToLogin()
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
                text = stringResource(R.string.enter),
                color = AppColors.Secondary,
                style = AppTypography.title.copy(fontSize = 14.sp),
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        val annotatedString = buildAnnotatedString {
            withStyle(style = SpanStyle(color = Color.White, fontSize = 14.sp)) {
                append(stringResource(R.string.privacy_text_start))
                append("\n")
            }

            pushStringAnnotation(tag = "URL", annotation = "https://docs.google.com/document/d/1j5fQgOuJsxGygoJ3G4PW2ccAlLxSk71XrlIoGCSaC88/edit?usp=sharing")
            withStyle(style = SpanStyle(color = Color.Red, fontSize = 14.sp)) {
                append(stringResource(R.string.privacy_link_text))
            }
            pop()
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