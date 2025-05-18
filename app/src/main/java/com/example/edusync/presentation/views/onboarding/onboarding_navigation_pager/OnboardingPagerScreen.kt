package com.example.edusync.presentation.views.onboarding.onboarding_navigation_pager

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.onboarding.OnboardingViewModel
import com.example.edusync.presentation.views.onboarding.onboardingIndicator.OnboardingIndicator
import com.example.edusync.presentation.views.onboarding.onboardingScreen.OnboardingScreen
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingPagerScreen() {
    val viewModel = koinViewModel<OnboardingViewModel>()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = AppColors.Background)
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingScreen(
                    image = R.drawable.onboarding_screen1,
                    title = stringResource(R.string.onboarding_title_schedule),
                    description = stringResource(R.string.onboarding_desc_schedule)
                )

                1 -> OnboardingScreen(
                    image = R.drawable.onboarding_screen2,
                    title = stringResource(R.string.onboarding_title_chat),
                    description = stringResource(R.string.onboarding_desc_chat)
                )

                2 -> OnboardingScreen(
                    image = R.drawable.onboarding_screen3,
                    title = stringResource(R.string.onboarding_title_files),
                    description = stringResource(R.string.onboarding_desc_files)
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
                        viewModel.goToLogin()
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
                Text(text = stringResource(R.string.next), color = AppColors.Background)
            }
        }
    }
}