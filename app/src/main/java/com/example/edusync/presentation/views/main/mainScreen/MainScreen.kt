package com.example.edusync.presentation.views.main.mainScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
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
import com.example.edusync.data.local.SelectedScheduleStorage
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

@RequiresApi(Build.VERSION_CODES.O)
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
    val isTeacherSchedule by viewModel.isTeacherScheduleVisible.collectAsState()

    val retrySchedule = {
        val groupIdRetry = SelectedScheduleStorage.selectedGroupId
        val groupNameRetry = SelectedScheduleStorage.selectedGroupName
        val teacherIdRetry = SelectedScheduleStorage.selectedTeacherId
        val teacherInitialsRetry = SelectedScheduleStorage.selectedTeacherInitials

        when {
            groupIdRetry != null && groupNameRetry != null -> viewModel.setSelectedGroup(groupIdRetry, groupNameRetry)
            teacherIdRetry != null && teacherInitialsRetry != null -> viewModel.setSelectedTeacher(teacherIdRetry, teacherInitialsRetry)
        }
    }

    LaunchedEffect(Unit) {
        if (state.schedule == null && state.selectedGroup == null && state.selectedTeacher == null) {
            if (groupId != null && groupName != null) {
                viewModel.setSelectedGroup(groupId, groupName)
            } else if (teacherId != null && teacherName != null) {
                viewModel.setSelectedTeacher(teacherId, teacherName)
            } else {
                val selectedGroupId = SelectedScheduleStorage.selectedGroupId
                val selectedGroupName = SelectedScheduleStorage.selectedGroupName
                val selectedTeacherId = SelectedScheduleStorage.selectedTeacherId
                val selectedTeacherName = SelectedScheduleStorage.selectedTeacherInitials

                when {
                    selectedGroupId != null && selectedGroupName != null -> {
                        viewModel.setSelectedGroup(selectedGroupId, selectedGroupName)
                    }

                    selectedTeacherId != null && selectedTeacherName != null -> {
                        viewModel.setSelectedTeacher(selectedTeacherId,     selectedTeacherName)
                    }

                    else -> {
                        val user = viewModel.getUser()
                        if (user?.isTeacher == true) {
                            val teacherId = viewModel.getTeacherId()

                            val teacherInitialsList = viewModel.teacherInitialsList.value

                            Log.d("MainScreen", "teacherInitialsList: $teacherInitialsList")

                            val generated = viewModel.generateTeacherInitials(user.fullName)
                                .replace("\\s+".toRegex(), " ")  // ← нормализуем
                                .trim()

                            teacherInitialsList.forEach {
                                val original = it.initials
                                val normalized = original.replace("\\s+".toRegex(), " ").trim()
                                Log.d("MainScreen", "API: '$normalized' vs Generated: '$generated'")
                            }

                            val matched = teacherInitialsList.firstOrNull {
                                it.initials.replace("\\s+".toRegex(), " ").trim() == generated
                            }

                            Log.d("MainScreen", "teacherId: $teacherId")

                            if (teacherId != null && matched != null) {
                                viewModel.setSelectedTeacher(matched.id, matched.initials)
                            }
                        } else {
                            val groupId = user?.groupId

                            val groupName = viewModel.getGroupNameById(groupId)

                            if (groupId != null && groupName != null) {
                                viewModel.setSelectedGroup(groupId, groupName)
                            }
                        }
                    }
                }
            }
        }
    }

    Column(
        Modifier
            .fillMaxSize()
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
                .clickable {
                    if (!isAllScheduleVisible) viewModel.showAllSchedule()
                    else viewModel.showSchedule()
                }
        )

        if (isAllScheduleVisible) {
            AllWeekScheduleLayout()
        } else {
            when (state.scheduleLoading) {
                LoadingState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }

                LoadingState.Success -> {
                    state.schedule?.let { schedule ->
                        if (schedule.days.isEmpty()) {
                            EmptyScheduleScreen(onRetry = retrySchedule)
                        } else {
                            ScheduleLayout(
                                data = schedule,
                                viewModel = viewModel,
                                onDeleteClick = { viewModel.deletePair(it) },
                                isTeacher = isTeacher,
                                isTeacherSchedule = isTeacherSchedule
                            )
                        }
                    } ?: EmptyScheduleScreen(onRetry = retrySchedule)
                }

                LoadingState.Empty -> EmptyScheduleScreen(onRetry = retrySchedule)
                is LoadingState.Error -> {
                    EmptyScheduleScreen(onRetry = retrySchedule)
                }
            }
        }
    }
}