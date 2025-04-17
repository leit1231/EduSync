package com.example.edusync.presentation.views.main.component.day_schedule_card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.domain.model.schedule.Day
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import com.example.edusync.presentation.views.main.component.dateItem.toDayOfWeek
import com.example.edusync.presentation.views.main.component.dateItem.toFullMonth
import java.util.Calendar

@Composable
fun DayScheduleCard(day: Day, expandedDays: MutableMap<String, Boolean>) {
    val isExpanded = expandedDays[day.isoDateDay] ?: false
    val date = day.isoDateDay.toCalendar()
    val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)
    val month = date.toFullMonth().lowercase().replaceFirstChar { it.uppercase() }
    val dayOfWeek = date.toDayOfWeek().lowercase().replaceFirstChar { it.uppercase() }

    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Card(
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, AppColors.Primary),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expandedDays[day.isoDateDay] = !isExpanded },
            colors = CardDefaults.cardColors(containerColor = AppColors.Background)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$dayOfMonth $month, $dayOfWeek",
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 16.sp),
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = AppColors.Secondary
                )
            }
        }

        if (isExpanded) {
            Card(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, AppColors.Primary),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.Background)
            ) {
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    day.pairs.forEachIndexed { index, pair ->
                        PairItemCardContent(pair, index + 1)
                        if (index < day.pairs.size - 1) {
                            HorizontalDivider(
                                thickness = 1.dp,
                                color = AppColors.Primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}