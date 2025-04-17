package com.example.edusync.presentation.views.main.component.scheduleScreen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import com.example.edusync.presentation.views.main.component.dateItem.toDayOfWeek
import com.example.edusync.presentation.views.main.component.dateItem.toFullMonth
import com.example.edusync.domain.model.schedule.Schedule
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatesPopup(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    list: Schedule,
    onItemClick: (Int) -> Unit,
    deleteIndex: Int,
    width: Dp,
) {
    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(surface = AppColors.Background),
    ) {
        DropdownMenu(
            modifier = Modifier
                .width(width)
                .border(1.dp, Color(0xFF417B65).copy(0.35f), RoundedCornerShape(12.dp)),
            expanded = expanded,
            onDismissRequest = onDismissRequest
        ) {
            CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                list.days.forEachIndexed { index, day ->
                    if (index != deleteIndex) {
                        DropdownMenuItem(
                            modifier = Modifier.height(40.dp),
                            contentPadding = PaddingValues(5.dp),
                            onClick = { onItemClick(index) },
                            text = {
                                val date = remember { day.isoDateDay.toCalendar() }
                                Text(
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center,
                                    text = "${date.get(Calendar.DAY_OF_MONTH)} ${date.toFullMonth()} ${date.toDayOfWeek()}",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight(700),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = AppColors.Secondary,
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
