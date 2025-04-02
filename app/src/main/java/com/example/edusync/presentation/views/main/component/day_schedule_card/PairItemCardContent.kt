package com.example.edusync.presentation.views.main.component.day_schedule_card

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.views.main.shedule.PairItem

@Composable
fun PairItemCardContent(pair: PairItem, number: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "$number. ${pair.pairInfo.first().doctrine}",
            color = AppColors.Secondary,
            style = AppTypography.body1.copy(fontSize = 16.sp),
        )
        Text(
            text = pair.pairInfo.first().teacher,
            color = AppColors.SecondaryTransparent,
            style = AppTypography.body1.copy(fontSize = 14.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
        Text(
            text = "${pair.pairInfo.first().auditoria} ауд.",
            color = AppColors.SecondaryTransparent,
            style = AppTypography.body1.copy(fontSize = 14.sp),
        )
        Text(
            text = pair.time,
            color = AppColors.Secondary,
            style = AppTypography.body1.copy(fontSize = 14.sp),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}