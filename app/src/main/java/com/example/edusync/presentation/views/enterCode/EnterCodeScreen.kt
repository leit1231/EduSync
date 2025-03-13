package com.example.edusync.presentation.views.enterCode

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.components.custom_text_field.otp_input_field.OtpInputField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.enterCode.EnterCodeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun EnterCodeScreen(
    navController: NavController
) {
    val viewModel: EnterCodeViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val focusRequesters = remember { List(6) { FocusRequester() } }

    Scaffold(topBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AppColors.Background)
        )
    }, bottomBar = {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = AppColors.Background)
        )
    }, modifier = Modifier
        .statusBarsPadding()
        .windowInsetsPadding(WindowInsets.navigationBars), content = { paddingValues ->
        LaunchedEffect(state.focusedIndex) {
            state.focusedIndex?.let { focusRequesters[it].requestFocus() }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .windowInsetsPadding(WindowInsets.ime)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .clickable {
                            navController.popBackStack()
                        }
                        .size(30.dp),
                    tint = AppColors.Primary
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "Введите код",
                    textAlign = TextAlign.Center,
                    style = AppTypography.title.copy(fontSize = 24.sp),
                    color = AppColors.Secondary
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.code.forEachIndexed { index, number ->
                    OtpInputField(
                        number = number,
                        focusRequester = focusRequesters[index],
                        onFocusChanged = { focused ->
                            if (focused) viewModel.onFocusChanged(index)
                        },
                        onNumberChanged = { newNumber ->
                            viewModel.onNumberEntered(newNumber, index)
                        },
                        onKeyboardBack = { viewModel.onBackspacePressed() },
                        modifier = Modifier
                            .weight(1f)
                            .height(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            val isButtonEnabled = state.code.all { it != null }
            Button(
                onClick = {
                    val code = state.code.joinToString("") { it?.toString().orEmpty() }
                    viewModel.sendCode(code)
                    navController.navigate(NavRoutes.ChangePassword.route)
                },
                enabled = isButtonEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Primary,
                    disabledContainerColor = AppColors.SecondaryTransparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "Далее",
                    style = AppTypography.body1.copy(fontSize = 14.sp),
                    color = colorResource(
                        id = if (isButtonEnabled) R.color.background
                        else R.color.background
                    )
                )
            }
        }
    })
}