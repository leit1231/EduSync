package com.example.edusync.presentation.views.main.shedule

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun EmptyScheduleScreen(){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        Icon(painter = painterResource(R.drawable.ic_no_schedule), contentDescription = null, tint = AppColors.Secondary, modifier = Modifier.size(160.dp))

        Text(
            text = "Расписание отсутствует",
            modifier = Modifier.padding(top = 20.dp),
            style = AppTypography.body1.copy(fontSize = 22.sp),
            color = AppColors.Secondary,
            textAlign = TextAlign.Center
        )
    }
}