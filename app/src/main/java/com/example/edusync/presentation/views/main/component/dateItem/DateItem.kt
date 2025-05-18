package com.example.edusync.presentation.views.main.component.dateItem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.domain.model.schedule.Day
import java.util.Calendar

@Composable
fun DateItem(day: Day, onClick: () -> Unit, onLeftClick: () -> Unit, onRightClick: () -> Unit, modifier: Modifier = Modifier) {
    val date = remember { day.isoDateDay.toCalendar() }
    val dayOfWeek = date.toDayOfWeek()
    val dayOfMonth = date.get(Calendar.DAY_OF_MONTH)
    val month = date.toFullMonth()

    Row(
        modifier = modifier
            .height(48.dp)
            .background(AppColors.Background, RoundedCornerShape(12.dp)),
        horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            modifier = Modifier
                .size(20.dp)
                .rotate(180F)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false),
                    onClick = onLeftClick
                ),
            painter = painterResource(id = R.drawable.ic_arrow_go),
            contentDescription = "Previous day",
            tint = AppColors.Secondary,
        )

        Column(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(),
                    onClick = onClick
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = dayOfWeek,
                style = AppTypography.body1.copy(fontSize = 14.sp),
                color = AppColors.Secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$dayOfMonth $month",
                style = AppTypography.body1.copy(fontSize = 14.sp),
                color = AppColors.Secondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            modifier = Modifier
                .size(20.dp)
                .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = false),
                onClick = onRightClick
            ),
            painter = painterResource(id = R.drawable.ic_arrow_go),
            contentDescription = "Next day",
            tint = AppColors.Secondary,
        )
    }
}