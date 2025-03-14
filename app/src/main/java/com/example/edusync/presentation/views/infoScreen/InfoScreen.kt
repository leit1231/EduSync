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
import androidx.navigation.NavController
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.infoStudent.InfoStudentViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun InfoStudentScreen(navController: NavController) {
    val viewModel: InfoStudentViewModel = koinViewModel()
    val uiState by viewModel.uiState
    val expandedUniversity = viewModel.expandedUniversity
    val universities = listOf("РКСИ", "ДГТУ", "РИНХ")
    val isTeacher = true

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

        GenericTextField(value = uiState.surname, onValueChange = viewModel::onSurnameChange, label = "Фамилия")

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = "Имя")

        Spacer(modifier = Modifier.height(16.dp))

        GenericTextField(value = uiState.patronymic, onValueChange = viewModel::onPatronymicChange, label = "Отчество")

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

        if (uiState.selectedUniversity.isNotEmpty() && isTeacher == false) {
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
            onClick = {
                navController.navigate(NavRoutes.MainScreen.route)
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = AppColors.Primary
            ),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            Text(
                text = "Сохранить",
                style = AppTypography.body1.copy(fontSize = 14.sp),
            )
        }
    }
}