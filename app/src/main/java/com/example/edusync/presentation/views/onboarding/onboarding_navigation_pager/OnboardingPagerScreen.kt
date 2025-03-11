package com.example.edusync.presentation.views.onboarding.onboarding_navigation_pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.views.onboarding.onboardingIndicator.OnboardingIndicator
import com.example.edusync.presentation.views.onboarding.onboardingScreen.OnboardingScreen
import kotlinx.coroutines.launch

@Composable
fun OnboardingPagerScreen(navController: NavController) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = AppColors.Background)
                    .padding(16.dp)
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .windowInsetsPadding(WindowInsets.navigationBars)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppColors.Background)
                .padding(paddingValues)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                when (page) {
                    0 -> OnboardingScreen(
                        image = R.drawable.onboarding_screen1,
                        title = "Просмотр и изменение расписания",
                        description = "Возможность просматривать и отмечать отдельные пары как особенные"
                    )
                    1 -> OnboardingScreen(
                        image = R.drawable.onboarding_screen2,
                        title = "Общайтесь с преподавателем",
                        description = "Теперь можно общаться с преподавателем прямо в приложении"
                    )
                    2 -> OnboardingScreen(
                        image = R.drawable.onboarding_screen3,
                        title = "Все файлы в одном месте",
                        description = "Все важные файлы будут храниться в одном месте"
                    )
                }
            }

            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 32.dp)
            ) {
                OnboardingIndicator(currentStep = pagerState.currentPage, 3)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = {
                        if (pagerState.currentPage < 2) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            navController.navigate("login")
                        }
                    },
                    modifier = Modifier
                        .size(380.dp, 40.dp)
                        .align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = AppColors.Primary
                    ),
                    shape = RoundedCornerShape(100.dp),
                ) {
                    Text(text = "Далее", color = AppColors.Background)
                }
            }
        }
    }
}