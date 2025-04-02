package com.example.edusync.presentation.views.materials

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.components.custom_text_field.search_field.SearchField
import com.example.edusync.presentation.components.modal_window.JoinGroupModalWindow
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.materials.MaterialsScreenViewModel
import com.example.edusync.presentation.views.materials.component.GroupItem
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu
import org.koin.androidx.compose.koinViewModel

@Composable
fun MaterialsScreen(navController: NavHostController) {

    val viewModel: MaterialsScreenViewModel = koinViewModel()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: "materials_screen"
    val groups by remember { mutableStateOf(viewModel.groups) }
    val searchQuery = remember { mutableStateOf("") }
    val isTeacher = true
    val isModalDialogVisible = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = {
            NavigationMenu(navController, currentRoute)
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {
                Text(
                    text = "Материалы",
                    style = AppTypography.title.copy(fontSize = 24.sp),
                    color = AppColors.Secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(32.dp))
                SearchField(
                    label = "Поиск предмета по преподавателю",
                    value = searchQuery.value,
                    onValueChange = { newValue ->
                        searchQuery.value = newValue
                    },
                    onSearch = {
                    },
                    imeAction = ImeAction.Search,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.height(24.dp))
                if (groups.isEmpty()) {
                    EmptyMaterialsScreen(navController)
                } else {
                    LazyColumn(modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)) {
                        items(groups) { group ->
                            GroupItem(group, navController)
                        }
                    }
                }

                if (isTeacher) {
                    Button(
                        onClick = {
                            navController.navigate(NavRoutes.CreateGroupScreen.route)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = AppColors.SecondaryTransparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Добавить группу",
                            style = AppTypography.body1.copy(fontSize = 14.sp),
                            color = AppColors.Background
                        )
                    }
                }else{
                    Button(
                        onClick = {
                            isModalDialogVisible.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AppColors.Primary,
                            disabledContainerColor = AppColors.SecondaryTransparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Text(
                            text = "Присоединиться к группе",
                            style = AppTypography.body1.copy(fontSize = 14.sp),
                            color = AppColors.Background
                        )
                    }
                }
            }
        }
    )
    if (isModalDialogVisible.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
                .clickable { isModalDialogVisible.value = false }
        ) {
            JoinGroupModalWindow(
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}