package com.example.edusync.presentation.theme.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

val DarkColorScheme = darkColorScheme(
    primary = AppColors.Primary,
    onPrimary = AppColors.OnBackground,
    secondary = AppColors.Secondary,
    onSecondary = AppColors.SecondaryTransparent,
    background = AppColors.Background,
    onBackground = AppColors.OnBackground,
    error = AppColors.Error,
)

@Composable
fun EduSyncTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(
            bodyLarge = AppTypography.body1,
            headlineLarge = AppTypography.title,
            labelSmall = AppTypography.caption,
            bodyMedium = AppTypography.lightText,
            headlineMedium = AppTypography.mediumItalic
        ),
        content = content
    )
}