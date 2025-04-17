package com.example.edusync.presentation.views.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.components.modal_window.LogoutWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.profile.ProfileScreenViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun ProfileScreen() {
    val viewModel: ProfileScreenViewModel = koinViewModel()
    val uiState by viewModel.uiState
    val expandedUniversity = viewModel.expandedUniversity
    val universities = listOf("РКСИ", "ДГТУ", "РИНХ")
    val isTeacher = false
    val isLogoutDialogVisible by viewModel.isLogoutDialogVisible

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "Профиль",
                modifier = Modifier.offset(x = 16.dp),
                textAlign = TextAlign.Center,
                style = AppTypography.title.copy(fontSize = 24.sp),
                color = AppColors.Secondary
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                painter = painterResource(id = R.drawable.ic_settings),
                contentDescription = "Settings",
                modifier = Modifier
                    .clickable {
                        viewModel.goToSettings()
                    }
                    .size(30.dp),
                tint = AppColors.Primary
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        GenericTextField(
            value = uiState.surname,
            onValueChange = viewModel::onSurnameChange,
            label = "Фамилия"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(
            value = uiState.name,
            onValueChange = viewModel::onNameChange,
            label = "Имя"
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(
            value = uiState.patronymic,
            onValueChange = viewModel::onPatronymicChange,
            label = "Отчество"
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            label = "Выберите ваше учебное заведение",
            options = universities,
            selectedOption = uiState.selectedUniversity,
            onOptionSelected = viewModel::onUniversitySelected,
            expanded = expandedUniversity,
            onExpandedChange = { viewModel.expandedUniversity = it },
            isChanged = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (!isTeacher) {
            CustomDropdownMenu(
                label = "Выберите вашу группу",
                options = viewModel.availableGroups,
                selectedOption = uiState.selectedGroup,
                onOptionSelected = viewModel::onGroupSelected,
                expanded = viewModel.expandedGroup,
                onExpandedChange = { viewModel.expandedGroup = it },
                isChanged = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Сохранить",
                style = AppTypography.body1.copy(fontSize = 14.sp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { viewModel.showLogoutDialog() },
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
                text = "Выйти из аккаунта",
                color = AppColors.Secondary,
                style = AppTypography.body1.copy(fontSize = 14.sp),
                maxLines = 1
            )
        }
    }


    if (isLogoutDialogVisible) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { viewModel.hideLogoutDialog()}
        ) {
            LogoutWindow(
                onClick = { viewModel.performLogout() },
                onDismiss = { viewModel.hideLogoutDialog() },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}