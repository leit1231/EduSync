package com.example.edusync.presentation.views.materials.group

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import com.example.edusync.presentation.components.custom_text_field.generic_text_field.GenericTextField
import com.example.edusync.presentation.components.modal_window.CreateGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.CreateGroupViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateGroupScreen(navController: NavController) {

    val viewModel: CreateGroupViewModel = koinViewModel()
    val uiState by viewModel.uiState
    val isModalWindowVisible = remember { mutableStateOf(false) }

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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(AppColors.Background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
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
                    text = "Создание группы",
                    textAlign = TextAlign.Center,
                    style = AppTypography.title.copy(fontSize = 24.sp),
                    color = AppColors.Secondary,
                    modifier = Modifier.offset(x = (-16).dp)
                )

                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(32.dp))

            GenericTextField(
                value = uiState.titleLesson,
                onValueChange = viewModel::onTitleLessonChange,
                label = "Название дисциплины"
            )

            Spacer(modifier = Modifier.height(16.dp))

            CustomDropdownMenu(
                label = "Выберите группу",
                options = viewModel.availableGroups,
                selectedOption = uiState.selectedGroup,
                onOptionSelected = viewModel::onGroupSelected,
                expanded = viewModel.expandedGroup,
                onExpandedChange = { viewModel.expandedGroup = it },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            GenericTextField(
                value = uiState.numberOfHours,
                onValueChange = viewModel::onNumbersOfHoursChange,
                label = "Введите количество часов дисциплины*"
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    isModalWindowVisible.value = true
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
    })
    if (isModalWindowVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { isModalWindowVisible.value = false }
        ) {
            CreateGroupModalWindow(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}