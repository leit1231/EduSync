package com.example.edusync.presentation.views.main.component.pair

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.domain.model.schedule.PairItem

@Composable
fun Pair(pair: PairItem, scrollInProgress: Boolean, onReminderClick: () -> Unit, isTeacherSchedule: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 130.dp)
            .background(AppColors.Background, RoundedCornerShape(12.dp))
            .border(1.dp, AppColors.Primary, RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            pair.pairInfo.first().warn.takeIf { it.isNotBlank() }?.let { warn ->
                Text(
                    text = "* $warn",
                    style = AppTypography.title.copy(fontSize = 14.sp),
                    color = AppColors.Secondary
                )
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(
                text = "${pair.pairInfo.first().number}. ${pair.pairInfo.first().doctrine}",
                style = AppTypography.body1.copy(fontSize = 14.sp),
                color = AppColors.Secondary
            )

            Column(modifier = Modifier.padding(top = 8.dp)) {
                if (isTeacherSchedule) {
                    pair.pairInfo.first().group.takeIf { it.isNotBlank() }?.let {
                        InfoRow(icon = R.drawable.ic_groups, text = it)
                    }
                }else{
                    pair.pairInfo.first().teacher.takeIf { it.isNotBlank() }?.let {
                        InfoRow(icon = R.drawable.ic_teacher, text = it)
                    }
                }
                pair.pairInfo.first().auditoria.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = R.drawable.ic_key, text = it)
                }
                pair.pairInfo.first().corpus.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = R.drawable.ic_hall, text = it)
                }
            }
        }
    }
}