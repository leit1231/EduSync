package com.example.edusync.presentation.views.main.component.pair

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun PageIndicator(modifier: Modifier = Modifier, count: Int, current: Int) {
    val enabled = AppColors.Primary
    val disabled = AppColors.SecondaryTransparent

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        repeat(count) { iteration ->
            Canvas(modifier = Modifier.size(5.dp), onDraw = {
                drawCircle(if (current == iteration) enabled else disabled)
            })
        }
    }
}