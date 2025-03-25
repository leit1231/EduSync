package com.example.edusync.presentation.views.main

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.views.main.component.day_schedule_card.DayScheduleCard
import org.koin.androidx.compose.koinViewModel

@Composable
fun AllScheduleLayout() {

    val viewModel: MainScreenViewModel = koinViewModel()
    val scheduleState by viewModel.state.collectAsState()
    val expandedDays = remember { mutableStateMapOf<String, Boolean>() }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars),
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                LazyColumn {
                    scheduleState.schedule?.days?.forEach { day ->
                        item {
                            DayScheduleCard(day, expandedDays)
                        }
                    }
                }
            }
        }
    )
}
