package com.example.edusync.presentation.views.favorities

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun EmptyFavoritesScreen() {

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_no_favorite),
            contentDescription = "Empty Favorite",
            tint = AppColors.Secondary
        )
        Text(
            text = "У вас пока нет избранных файлов,\n" +
                    " нажмите на файл и кликните на знак избранного",
            textAlign = TextAlign.Center,
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary
        )
    }
}