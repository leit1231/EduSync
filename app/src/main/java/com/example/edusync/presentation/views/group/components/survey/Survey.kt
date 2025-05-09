package com.example.edusync.presentation.views.group.components.survey

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.presentation.theme.ui.AppColors
import kotlin.math.roundToInt

@Composable
fun Survey(
    question: String,
    totalCount: Int,
    lineColor: Color = AppColors.Primary,
    lineBackgroundColor: Color = Color.LightGray,
    answers: List<AnswerData>,
    selected: AnswerData? = null,
    onClick: (AnswerData) -> Unit
) {
    val percentTextStyle = remember {
        TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = AppColors.Secondary
        )
    }

    val checkBoxSize = calculateCheckboxSize(percentTextStyle)
    val maxValue = answers.maxOf { it.count }

    val fractionAnimator = animateFloatAsState(
        targetValue = if (selected != null) 1f else 0f,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "SurveyFractionAnimation"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = question,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            color = AppColors.Secondary
        )

        answers.forEach {
            val isSelected = selected?.id == it.id
            Answer(
                text = it.text,
                textStyle = LocalTextStyle.current,
                lineHeight = 4.dp,
                lineColor = lineColor,
                lineBackgroundColor = lineBackgroundColor,
                percent = it.calculatePercent(totalCount),
                fraction = it.calculateFraction(maxValue) * fractionAnimator.value,
                selected = isSelected,
                showResult = true,
                percentTextStyle = percentTextStyle,
                checkBoxSize = checkBoxSize
            ) {
                if (selected == null || selected.id == it.id) {
                    onClick(it)
                }
            }
        }
    }
}

@Composable
fun Answer(
    text: String,
    textStyle: TextStyle = LocalTextStyle.current,
    checkBoxSize: Dp,
    percentTextStyle: TextStyle,
    selected: Boolean,
    showResult: Boolean,
    lineHeight: Dp,
    lineColor: Color = AppColors.Primary,
    lineBackgroundColor: Color = Color.LightGray,
    percent: Int,
    fraction: Float,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        SurveyCheckBox(
            modifier = Modifier.size(checkBoxSize + 2.dp),
            percent = percent,
            selected = selected,
            showResult = showResult,
            color = AppColors.Primary,
            textStyle = percentTextStyle
        )
        Box(
            modifier = Modifier.weight(1f)
        ) {

            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                Spacer(
                    modifier = Modifier.height(lineHeight)
                )
                Text(
                    modifier = Modifier,
                    text = text,
                    style = textStyle,
                    color = AppColors.Secondary
                )
                Spacer(
                    modifier = Modifier.height(lineHeight)
                )
            }
            Line(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(lineHeight)
                    .align(Alignment.BottomCenter),
                lineHeight = lineHeight,
                lineColor = lineColor,
                lineBackgroundColor = lineBackgroundColor,
                fraction = fraction
            )
        }
    }
}

@Composable
private fun SurveyCheckBox(
    modifier: Modifier,
    percent: Int,
    selected: Boolean,
    showResult: Boolean,
    color: Color,
    textStyle: TextStyle
) {
    AnimatedContent(
        modifier = modifier,
        targetState = showResult,
        label = ""
    ) {
        if (!it)
            Canvas(
                modifier = Modifier
                    .fillMaxHeight()
                    .aspectRatio(1f)
            ) {
                drawCircle(
                    color = Color.LightGray,
                    radius = size.minDimension * 0.3f,
                    style = Stroke(1.dp.toPx())
                )
            }
        else
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$percent%",
                    style = textStyle,
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .aspectRatio(1f)
                        .then(
                            if (selected)
                                Modifier.background(color, CircleShape)
                            else
                                Modifier.border(
                                    1.dp,
                                    Color.LightGray,
                                    CircleShape
                                )
                        )

                )
            }
    }
}

@Composable
private fun Line(
    modifier: Modifier,
    lineHeight: Dp,
    lineColor: Color = AppColors.Primary,
    lineBackgroundColor: Color = Color.LightGray,
    fraction: Float
) {
    Canvas(
        modifier = modifier
    ) {
        if (fraction > 0) {
            val cornerRadiusPx = lineHeight.toPx()
            drawRoundRect(
                color = lineBackgroundColor,
                cornerRadius = CornerRadius(cornerRadiusPx)
            )
            drawRoundRect(
                color = lineColor,
                cornerRadius = CornerRadius(cornerRadiusPx),
                size = size.copy(
                    width = (size.width * fraction).coerceAtLeast(cornerRadiusPx)
                )
            )
        }
    }
}

@Immutable
data class AnswerData(
    val id: Int = 0,
    val text: String,
    val count: Int
) {
    fun calculateFraction(maxValue: Int): Float {
        return if (maxValue == 0) 0f else count.toFloat() / maxValue
    }

    fun calculatePercent(totalCount: Int): Int {
        return if (totalCount == 0) 0
        else ((count.toFloat() / totalCount) * 100).roundToInt()
    }
}

@Composable
fun calculateCheckboxSize(
    style: TextStyle = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
): Dp {
    val density = LocalDensity.current
    val measurer = rememberTextMeasurer()
    return density.run { measurer.measure(text = "100%", style = style).size.width.toDp() }
}
