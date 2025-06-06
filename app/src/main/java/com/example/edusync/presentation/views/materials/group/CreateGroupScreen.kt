package com.example.edusync.presentation.views.materials.group

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.components.custom_text_field.dropdownMenu.CustomDropdownMenu
import com.example.edusync.presentation.components.modal_window.CreateGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.CreateGroupViewModel
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun CreateGroupScreen() {
    val viewModel: CreateGroupViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val groups by viewModel.groups.collectAsState()
    val subjects = viewModel.subjectNames
    val isModalWindowVisible = remember { mutableStateOf(false) }
    val modalLink = remember { mutableStateOf("") }
    val expandedGroup by viewModel.expandedGroup.collectAsState()
    val expandedSubject by viewModel.expandedSubject.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                text = stringResource(R.string.create_group),
                textAlign = TextAlign.Center,
                style = AppTypography.title.copy(fontSize = 24.sp),
                color = AppColors.Secondary,
                modifier = Modifier.offset(x = (-16).dp)
            )

            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(32.dp))

        CustomDropdownMenu(
            label = stringResource(R.string.select_group_on_create),
            options = groups.map { it.name },
            selectedOption = uiState.selectedGroup,
            onOptionSelected = viewModel::onGroupSelected,
            expanded = expandedGroup,
            onExpandedChange = { viewModel.expandedGroup.value = it },
            modifier = Modifier.fillMaxWidth(),
            isChanged = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        CustomDropdownMenu(
            label = stringResource(R.string.select_subject),
            options = subjects,
            selectedOption = uiState.selectedSubject,
            onOptionSelected = viewModel::onSubjectSelected,
            expanded = expandedSubject,
            onExpandedChange = { viewModel.expandedSubject.value = it },
            modifier = Modifier.fillMaxWidth(),
            isChanged = true
        )

        Spacer(modifier = Modifier.weight(1f))

        if (uiState.errorMessage.isNotBlank()) {
            Text(
                text = uiState.errorMessage,
                color = Color.Red,
                style = AppTypography.body1.copy(fontSize = 14.sp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp)
            )
        }
        Button(
            onClick = {
                viewModel.createChat(
                    onSuccess = {
                        modalLink.value = it
                        isModalWindowVisible.value = true
                    },
                    onError = {
                    }
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = AppColors.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            Text(
                text = stringResource(R.string.save),
                style = AppTypography.body1.copy(fontSize = 14.sp),
            )
        }
    }

    if (isModalWindowVisible.value && modalLink.value.isNotBlank()) {
        BackHandler(enabled = true) {}
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            CreateGroupModalWindow(
                modifier = Modifier.align(Alignment.Center),
                link = modalLink.value,
                onContinue = {
                    isModalWindowVisible.value = false
                    coroutineScope.launch {
                        viewModel.goToMaterials()
                    }
                }
            )
        }
    }
}