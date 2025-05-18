package com.example.edusync.presentation.views.shared

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.eduHub.edusync.R
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun StateScreen(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    isError: Boolean = false,
    isEmpty: Boolean = false,
    emptyText: String = "",
    errorText: String = "",
    retryButtonText: String = stringResource(R.string.update),
    onRetry: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = AppColors.Primary)
            }

            isError -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_no_internet),
                        contentDescription = null,
                        modifier = Modifier.size(160.dp),
                        tint = AppColors.Secondary
                    )
                    Text(
                        text = errorText,
                        color = AppColors.Secondary,
                        style = AppTypography.body1.copy(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                    onRetry?.let {
                        Text(
                            text = retryButtonText,
                            color = AppColors.Primary,
                            style = AppTypography.body1.copy(fontSize = 18.sp),
                            modifier = Modifier
                                .padding(top = 16.dp)
                                .clickable { it() }
                        )
                    }
                }
            }

            isEmpty -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_no_schedule),
                        contentDescription = null,
                        modifier = Modifier.size(160.dp)
                    )
                    Text(
                        text = emptyText,
                        color = AppColors.Secondary,
                        style = AppTypography.body1.copy(fontSize = 20.sp),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
        }
    }
}