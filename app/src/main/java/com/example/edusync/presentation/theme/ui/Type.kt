package com.example.edusync.presentation.theme.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R

object AppTypography {
    val body1 = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.ubuntu_regular)
        ),
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    )
    val title = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.ubuntu_bold)
        ),
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp
    )
    val caption = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.ubuntu_italic)
        ),
        fontStyle = FontStyle.Italic,
        fontSize = 14.sp
    )
    val lightText = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.ubuntu_light)
        ),
        fontWeight = FontWeight.Light,
        fontSize = 16.sp
    )
    val mediumItalic = TextStyle(
        fontFamily = FontFamily(
            Font(R.font.ubuntu_medium)
        ),
        fontWeight = FontWeight(500),
        fontStyle = FontStyle.Italic,
        fontSize = 18.sp
    )
}