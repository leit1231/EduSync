package com.example.edusync.presentation.views.favorities.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.edusync.R
import com.example.edusync.domain.model.favorietesFiles.FileItem
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.theme.ui.AppTypography

@Composable
fun FileItemView(
    file: FileItem,
    onFavoriteToggle: () -> Unit,
    onDownloadToggle: () -> Unit,
    onFileOpen: () -> Unit
) {
    val extension = file.name.substringAfterLast('.', "").lowercase()
    val iconRes = when (extension) {
        "pdf" -> R.drawable.ic_pdf
        "doc", "docx" -> R.drawable.ic_doc
        "xls", "xlsx" -> R.drawable.ic_xls
        "txt" -> R.drawable.ic_txt
        "ppt", "pptx" -> R.drawable.ic_ppt
        else -> R.drawable.ic_null_file
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(AppColors.Background)
            .border(1.dp, AppColors.Primary, shape = RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        shape = RoundedCornerShape(10.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = "File Type",
                modifier = Modifier
                    .size(47.dp, 58.dp)
                    .clickable { onFileOpen() },
                tint = Color.Unspecified
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
                    .clickable { onFileOpen() },
                verticalArrangement = Arrangement.Top
            ) {
                Text(
                    text = file.name,
                    color = AppColors.Secondary,
                    style = AppTypography.body1.copy(fontSize = 16.sp)
                )
                Text(
                    text = file.size,
                    color = AppColors.SecondaryTransparent,
                    style = AppTypography.body1.copy(fontSize = 12.sp)
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_favorit_bold),
                    tint = if (file.isFavorite) AppColors.Error else AppColors.SecondaryTransparent,
                    contentDescription = "Favorite",
                    modifier = Modifier
                        .clickable { onFavoriteToggle() }
                        .padding(horizontal = 4.dp)
                        .size(24.dp)
                )
                Icon(
                    painter = painterResource(
                        id = if (file.isDownload) R.drawable.ic_delete else R.drawable.ic_download
                    ),
                    tint = AppColors.Primary,
                    contentDescription = "Download/Delete",
                    modifier = Modifier
                        .clickable { onDownloadToggle() }
                        .padding(start = 4.dp)
                        .size(24.dp)
                )
            }
        }
    }
}