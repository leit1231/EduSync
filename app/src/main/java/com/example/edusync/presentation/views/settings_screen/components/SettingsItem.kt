package com.example.edusync.presentation.views.settings_screen.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
    title: String,
    toggleState: Boolean? = null,
    onToggleChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 20.dp, horizontal = 16.dp)
            .clickable(enabled = onClick != null && onToggleChange == null) { onClick?.invoke() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = AppTypography.body1.copy(fontSize = 16.sp),
            color = AppColors.Secondary
        )

        if (toggleState != null && onToggleChange != null) {
            CompositionLocalProvider(
                LocalMinimumInteractiveComponentEnforcement provides false
            ) {
                Switch(
                    checked = toggleState,
                    onCheckedChange = onToggleChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AppColors.Background,
                        checkedTrackColor = AppColors.Primary,
                        uncheckedThumbColor = AppColors.Secondary,
                        uncheckedTrackColor = AppColors.SecondaryTransparent
                    ),
                    modifier = Modifier
                        .size(34.dp, 20.dp)
                        .padding(end = 16.dp),
                    thumbContent = {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .background(
                                    color = if (toggleState) AppColors.Background else AppColors.SecondaryTransparent,
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    }
                )
            }
        } else {
            Icon(
                modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.ic_arrow_go),
                contentDescription = null,
                tint = AppColors.Secondary
            )
        }
    }
}