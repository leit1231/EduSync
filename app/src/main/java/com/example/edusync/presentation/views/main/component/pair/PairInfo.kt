package com.example.edusync.presentation.views.main.component.pair

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.domain.model.schedule.PairInfo

@Composable
fun PairInfo(pair: PairInfo) {
    Column(
        Modifier
            .padding(end = 11.dp)
            .padding(vertical = 14.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 40.dp)
                .background(AppColors.Background, RoundedCornerShape(35.dp))
                .padding(horizontal = 15.dp, vertical = 6.dp),
            contentAlignment = Alignment.CenterStart,
        ) {
            Text(
                text = pair.doctrine,
                fontSize = if (pair.doctrine.length > 20) 14.sp else 18.sp,
                color = AppColors.Secondary,
            )
        }
        if (pair.teacher.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 11.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_teacher),
                    contentDescription = "",
                    tint = AppColors.Secondary,
                )
                Text(
                    text = pair.teacher,
                    fontSize = 14.sp,
                    color = AppColors.Secondary,
                )
            }
        }

        if (pair.auditoria.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_key),
                    contentDescription = "",
                    tint = AppColors.Secondary,
                )
                Text(
                    text = pair.auditoria,
                    fontSize = 14.sp,
                    color = AppColors.Secondary,
                )
            }
        }

        if (pair.corpus.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, top = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Icon(
                    modifier = Modifier.size(16.dp),
                    painter = painterResource(id = R.drawable.ic_hall),
                    contentDescription = "",
                    tint = AppColors.Secondary,
                )
                Text(
                    text = pair.corpus,
                    fontSize = 14.sp,
                    color = AppColors.Secondary,
                )
            }
        }
    }
}