package com.example.edusync.presentation.views.main.mainScreen

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.common.LoadingState
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.views.main.shedule.AllWeekScheduleLayout
import com.example.edusync.presentation.views.main.shedule.EmptyScheduleScreen
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import com.example.edusync.presentation.views.main.component.dateItem.toFullMonth
import com.example.edusync.presentation.views.main.component.scheduleScreen.ScheduleLayout
import com.example.edusync.presentation.views.main.component.select_group.SelectGroupTopBar
import org.koin.androidx.compose.koinViewModel
import java.util.Calendar

@Composable
fun MainScreen(
    groupId: Int? = null,
    groupName: String? = null,
    teacherId: Int? = null,
    teacherName: String? = null
) {

    val viewModel: MainScreenViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    val isAllScheduleVisible by viewModel.isAllScheduleVisible.collectAsState()
    val isTeacher by viewModel.isTeacher.collectAsState()

    LaunchedEffect(state.selectedGroup, state.selectedTeacher) {
        Log.d(
            "MainScreen",
            "Recomposing: group=${state.selectedGroup}, teacher=${state.selectedTeacher}"
        )
    }

    Column(
        Modifier
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
            selectedGroup = state.selectedGroup,
            selectedTeacher = state.selectedTeacher,
            onGroupClick = {
                viewModel.clearTeacher()
                viewModel.goToSearch(false)
            },
            onTeacherClick = {
                viewModel.clearGroup()
                viewModel.goToSearch(true)
            }
        )



        val scheduleDays = state.schedule?.days ?: emptyList()
        val startDate = scheduleDays.firstOrNull()?.isoDateDay?.toCalendar()
        val endDate = scheduleDays.lastOrNull()?.isoDateDay?.toCalendar()

        val periodText = if (startDate != null && endDate != null) {
            "${startDate.get(Calendar.DAY_OF_MONTH)} ${startDate.toFullMonth()} - " +
                    "${endDate.get(Calendar.DAY_OF_MONTH)} ${endDate.toFullMonth()}"
        } else ""

        Text(
            text = if (isAllScheduleVisible) periodText else "Пары на неделю",
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { if (!isAllScheduleVisible) viewModel.showAllSchedule() else viewModel.showSchedule() }
        )

        if (isAllScheduleVisible) {
            AllWeekScheduleLayout()
        } else {
            when (state.scheduleLoading) {
                LoadingState.Loading -> EmptyMainScreen()
                LoadingState.Success -> {
                    state.schedule?.let { schedule ->
                        if (schedule.days.isEmpty()) {
                            EmptyScheduleScreen()
                        } else {
                            ScheduleLayout(
                                data = schedule,
                                viewModel = viewModel,
                                onEditClick = { viewModel.setSelectedPair(it) },
                                onDeleteClick = { viewModel.deletePair(it) },
                                isTeacher = isTeacher
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

    LaunchedEffect(groupId, groupName, teacherId, teacherName) {
        if (groupId != null && groupName != null) {
            viewModel.setSelectedGroup(groupId, groupName)
        }
        if (teacherId != null && teacherName != null) {
            viewModel.setSelectedTeacher(teacherId, teacherName)
        }

    }
}