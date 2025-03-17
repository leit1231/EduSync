package com.example.edusync.presentation.views.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.edusync.common.NavRoutes
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.mainScreen.MainScreenViewModel
import com.example.edusync.presentation.views.main.component.lesson_component.LessonComponent
import com.example.edusync.presentation.views.main.component.lesson_component.SearchComponent
import com.example.edusync.presentation.views.navigation_menu.NavigationMenu
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
    navController: NavHostController
) {
    val viewModel: MainScreenViewModel = koinViewModel()
    val schedule by viewModel.schedule.collectAsState()
    val selectedDate by viewModel.selectedDate.collectAsState()
    val canSwipeBack by viewModel.canSwipeBack.collectAsState()
    val currentRoute = remember { mutableStateOf(NavRoutes.MainScreen) }
    val formatter = DateTimeFormatter.ofPattern("EEEE, d MMMM")
    val formattedDate = selectedDate.format(formatter)

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
            )
        },
        bottomBar = { NavigationMenu(navController, currentRoute.value.toString()) },
        modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121212))
                .padding(paddingValues)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { _, dragAmount ->
                        if (dragAmount < -50) viewModel.nextDay()
                        if (dragAmount > 50 && canSwipeBack) viewModel.previousDay()
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок и поиск
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppColors.Background)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Расписание",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(8.dp))
                SearchComponent()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Навигация по дням
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                IconButton(
                    onClick = { if (canSwipeBack) viewModel.previousDay() },
                    enabled = canSwipeBack
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Previous Day",
                        tint = if (canSwipeBack) Color.White else Color.Gray
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                        fontSize = 18.sp,
                        color = Color.White
                    )
                    Text(
                        text = formattedDate,
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }

                IconButton(
                    onClick = { viewModel.nextDay() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Next Day",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Список уроков
            val currentLessons = schedule.find {
                it.date == viewModel.getFormattedDate()
            }?.lessons ?: emptyList()

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                itemsIndexed(currentLessons) { index, lesson ->
                    LessonComponent(
                        lesson = lesson,
                        isTeacher = false,
                        onEditClick = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                if (currentLessons.isEmpty()) {
                    item {
                        Text(
                            text = "Нет пар на этот день",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }
                }
            }
        }
    }
}