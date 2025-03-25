package com.example.edusync.presentation.views.main

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import com.example.edusync.presentation.views.main.component.dateItem.toFullMonth
import com.example.edusync.presentation.views.main.component.scheduleLayout.ScheduleLayout
import com.example.edusync.presentation.views.main.component.select_group.SelectGroupTopBar
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun MainScreen(navController: NavHostController) {

    val viewModel: MainScreenViewModel = koinViewModel()
    val state = viewModel.state.collectAsState()
    val currentRoute = navController.currentBackStackEntry?.destination?.route ?: "main_screen"
    val isAllScheduleVisible by viewModel.isAllScheduleVisible.collectAsState()

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
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Расписание",
                        style = AppTypography.title.copy(fontSize = 24.sp),
                        color = AppColors.Secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 48.dp)
                    )

                    if (isAllScheduleVisible) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = "Назад",
                            tint = AppColors.Primary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .clickable { viewModel.showSchedule() }
                                .size(30.dp)
                        )
                    }
                }

                SelectGroupTopBar(
                    selectedGroup = state.value.selectedGroup ?: "Выбрать",
                    onGroupClick = { navController.navigate("search_screen") },
                    isTeacher = true
                )

                val scheduleDays = state.value.schedule?.days ?: emptyList()
                val startDate = scheduleDays.firstOrNull()?.isoDateDay?.toCalendar()
                val endDate = scheduleDays.lastOrNull()?.isoDateDay?.toCalendar()

                val periodText = if (startDate != null && endDate != null) {
                    "${startDate.get(Calendar.DAY_OF_MONTH)} ${startDate.toFullMonth()} - " +
                            "${endDate.get(Calendar.DAY_OF_MONTH)} ${endDate.toFullMonth()}"
                } else "Нет данных"

                Text(
                    text = if (isAllScheduleVisible) periodText else "Пары на неделю",
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    color = AppColors.Secondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.showAllSchedule() }
                )

                if (isAllScheduleVisible) {
                    AllScheduleLayout()
                } else {
                    when (state.value.scheduleLoading) {
                        LoadingState.Loading -> LoadingLayout()
                        LoadingState.Success -> {
                            state.value.schedule?.let { schedule ->
                                if (schedule.days.isEmpty()) {
                                    EmptyScheduleScreen()
                                } else {
                                    ScheduleLayout(
                                        data = schedule,
                                        viewModel = viewModel,
                                        onEditClick = { viewModel.setSelectedPair(it) },
                                        onDeleteClick = { viewModel.deletePair(it) }
                                    )
                                }
                            } ?: EmptyScheduleScreen()
                        }

                        LoadingState.Empty -> EmptyScheduleScreen()
                        is LoadingState.Error -> {
                        }
                    }
                }
            }
        }
    )
}