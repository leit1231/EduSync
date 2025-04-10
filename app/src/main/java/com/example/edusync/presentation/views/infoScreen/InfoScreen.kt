package com.example.edusync.presentation.views.infoScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel

@Composable
fun InfoScreen(viewModel: InfoStudentViewModel) {

    val uiState by viewModel.uiState
    val isTeacher = viewModel.role

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Заполнение профиля",
            color = AppColors.Secondary,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        GenericTextField(
            value = uiState.surname,
            onValueChange = { newValue ->
                viewModel.onSurnameChange(newValue)
                viewModel.validateSurname()
            },
            label = "Фамилия",
            isError = viewModel.surnameError.value != null,
            errorMessage = viewModel.surnameError.value
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(
            value = uiState.name,
            onValueChange = { newValue ->
                viewModel.onNameChange(newValue)
                viewModel.validateName()
            },
            label = "Имя",
            isError = viewModel.nameError.value != null,
            errorMessage = viewModel.nameError.value
        )

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(
            value = uiState.patronymic,
            onValueChange = { newValue ->
                viewModel.onPatronymicChange(newValue)
                viewModel.validatePatronymic()
            },
            label = "Отчество",
            isError = viewModel.patronymicError.value != null,
            errorMessage = viewModel.patronymicError.value
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            label = "Выберите ваше учебное заведение",
            options = uiState.availableUniversities,
            selectedOption = uiState.selectedUniversity,
            onOptionSelected = viewModel::onUniversitySelected,
            expanded = viewModel.expandedUniversity,
            onExpandedChange = { viewModel.expandedUniversity = it },
            isChanged = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (uiState.selectedUniversity.isNotEmpty() && !isTeacher) {
            CustomDropdownMenu(
                label = "Выберите вашу группу",
                options = uiState.availableGroups,
                selectedOption = uiState.selectedGroup,
                onOptionSelected = viewModel::onGroupSelected,
                expanded = viewModel.expandedGroup,
                onExpandedChange = { viewModel.expandedGroup = it },
                isChanged = true
            )
        }

        if (!uiState.error.isNullOrEmpty()) {
            Text(
                text = uiState.error!!,
                color = AppColors.Error,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {
                viewModel.registerUser()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = "Сохранить",
                style = AppTypography.body1.copy(fontSize = 14.sp),
            )
        }
    }
}