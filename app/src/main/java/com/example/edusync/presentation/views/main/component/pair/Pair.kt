package com.example.edusync.presentation.views.main.component.pair

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography
import com.example.edusync.presentation.views.main.PairInfo
import com.example.edusync.presentation.views.main.PairItem
import com.example.edusync.presentation.views.main.component.dateItem.toCalendar
import kotlinx.coroutines.delay

@Composable
fun PairItem(
    pair: PairItem,
    scrollInProgress: Boolean,
    onReminderClick: () -> Unit,
    isEditMode: Boolean,
    onEditClick: (PairItem) -> Unit,
    onDeleteClick: (PairItem) -> Unit
) {
    var currentMillis = System.currentTimeMillis()
    val startMillis = pair.isoDateStart.toCalendar().timeInMillis
    val endMillis = pair.isoDateEnd.toCalendar().timeInMillis
    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            currentMillis = System.currentTimeMillis()
        }
    }

    Box {
        if (currentMillis in startMillis until endMillis) {
            val defaultDuration = (endMillis - startMillis).toFloat()
            val progress = ((currentMillis - startMillis) / defaultDuration).coerceIn(0f, 1f)
            CurrentPair(progress, 1f - progress, pair, scrollInProgress)
        } else {
            Pair(
                pair = pair,
                scrollInProgress = scrollInProgress,
                onReminderClick = onReminderClick
            )
        }

        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 8.dp, bottom = 8.dp)
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(fontSize = 16.sp)) {
                            append(pair.pairInfo.first().start)
                        }
                        append("-\n")
                        withStyle(style = SpanStyle(fontSize = 14.sp)) {
                            append(pair.pairInfo.first().end)
                        }
                    },
                    color = AppColors.Secondary
                )
            }
        }

        if (isEditMode) {
            Box(modifier = Modifier.align(Alignment.TopEnd)) {
                IconButton(
                    onClick = { expanded = true }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_more),
                        contentDescription = "Опции",
                        tint = AppColors.Primary,
                        modifier = Modifier
                            .size(30.dp)
                    )
                }
                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = {
                            onEditClick(pair)
                            expanded = false
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = {
                            onDeleteClick(pair)
                            expanded = false
                        }
                    )
                }
            }
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit),
                contentDescription = "Set reminder",
                tint = AppColors.Primary,
                modifier = Modifier
                    .clickable { onReminderClick() }
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 16.dp)
                    .size(20.dp)
            )
        }
    }
}

@Composable
fun BoxScope.CurrentPair(progress: Float, end: Float, pair: PairItem, scrollInProgress: Boolean) {
    val pagerState = rememberPagerState { pair.pairInfo.size }
    val progressColor = AppColors.SecondaryTransparent
    val endColor = AppColors.Primary
    var height by remember { mutableIntStateOf(0) }
    Row(
    ) {
        Column(
            modifier = Modifier
                .width(55.dp)
                .height(with(LocalDensity.current) { height.toDp() }),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                fontSize = 20.sp,
                text = pair.pairInfo.first().start,
                color = progressColor,
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .background(progressColor)
                    .weight(progress),
            )
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .weight(end)
                    .background(endColor),
            )
            Text(
                fontSize = 20.sp,
                text = pair.pairInfo.first().start,
                color = progressColor,
            )

            Text(
                fontSize = 20.sp,
                text = pair.pairInfo.first().end,
                color = endColor,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .background(AppColors.Background, RoundedCornerShape(15.dp))
                .border(1.dp, AppColors.Primary, RoundedCornerShape(15.dp))
                .padding(start = 12.dp)
                .onGloballyPositioned {
                    height = it.size.height
                },
        ) {
            if (pair.pairInfo.size > 1) {
                HorizontalPager(
                    userScrollEnabled = !scrollInProgress,
                    state = pagerState,
                    flingBehavior = PagerDefaults.flingBehavior(
                        state = pagerState,
                        snapAnimationSpec = tween(
                            durationMillis = 200,
                            easing = LinearEasing
                        ),
                    ),
                ) {
                    PairInfo(pair.pairInfo[it])
                }
            } else {
                PairInfo(pair.pairInfo.first())
            }
        }
    }

    Text(
        modifier = Modifier
            .padding(bottom = 15.dp, end = 11.dp)
            .size(25.dp)
            .background(AppColors.Background, CircleShape)
            .align(Alignment.BottomEnd),
        fontSize = 20.sp,
        text = pair.pairInfo.first().number.toString(),
        color = Color.White,
        textAlign = TextAlign.Center,
    )

    if (pair.pairInfo.size > 1) {
        PageIndicator(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 6.dp),
            count = pair.pairInfo.size,
            current = pagerState.currentPage,
        )
    }
}

@Composable
fun Pair(pair: PairItem, scrollInProgress: Boolean, onReminderClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
                pair.pairInfo.first().teacher.takeIf { it.isNotBlank() }?.let {
                    InfoRow(icon = R.drawable.ic_teacher, text = it)
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

@Composable
private fun InfoRow(icon: Int, text: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
            tint = AppColors.SecondaryTransparent,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            style = AppTypography.body1.copy(fontSize = 14.sp),
            color = AppColors.SecondaryTransparent
        )
    }
}

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
