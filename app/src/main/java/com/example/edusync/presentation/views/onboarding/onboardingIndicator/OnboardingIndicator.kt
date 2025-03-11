package com.example.edusync.presentation.views.onboarding.onboardingIndicator

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun OnboardingIndicator(currentStep: Int, totalSteps: Int) {
    val indicatorNormalSize = 15.dp
    val indicatorSelectedSize = 30.dp
    val spacing = 8.dp

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        for (i in 0 until totalSteps) {
            val isSelected = i == currentStep
            val width by animateDpAsState(targetValue = if (isSelected) indicatorSelectedSize else indicatorNormalSize)
            val backgroundColor by animateColorAsState(targetValue = if (isSelected) AppColors.Primary else AppColors.Secondary)
            val borderColor by animateColorAsState(targetValue = if (isSelected) AppColors.Background else AppColors.SecondaryTransparent)

            Box(
                modifier = Modifier
                    .width(width)
                    .height(indicatorNormalSize)
                    .background(
                        color = backgroundColor,
                        shape = CircleShape
                    )
                    .border(
                        width = 1.dp,
                        color = borderColor,
                        shape = CircleShape
                    )
            )
            if (i < totalSteps - 1) {
                Spacer(modifier = Modifier.width(spacing))
            }
        }
    }
}