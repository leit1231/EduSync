package com.example.edusync.presentation.views.group.components.fileAttachment

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.edusync.R
import com.example.edusync.domain.model.message.FileAttachment
import com.example.edusync.presentation.theme.ui.AppColors
import com.example.edusync.presentation.viewModels.group.GroupViewModel

@Composable
fun FileAttachmentItem(
    file: FileAttachment,
    viewModel: GroupViewModel,
    onFileClicked: (Uri) -> Unit,
    isInSelectionMode: Boolean
) {
    val selectedFiles by viewModel.selectedFiles.collectAsStateWithLifecycle()
    val isSelected = selectedFiles.contains(file)
    val backgroundColor =
        if (isSelected) AppColors.Primary.copy(alpha = 0.2f) else Color.Transparent

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isInSelectionMode) {
                    viewModel.toggleFileSelection(file)
                } else {
                    onFileClicked(file.uri)
                }
            }
    )
    val extension = file.fileName.substringAfterLast('.', "").lowercase()
    val isImage = extension in setOf("png", "jpg", "jpeg")

    if (isImage) {
        AsyncImage(
            model = file.uri,
            contentDescription = "Изображение",
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(
                    when (extension) {
                        "pdf" -> R.drawable.ic_pdf
                        "doc", "docx" -> R.drawable.ic_doc
                        "xls", "xlsx" -> R.drawable.ic_xls
                        "txt" -> R.drawable.ic_txt
                        "ppt", "pptx" -> R.drawable.ic_ppt
                        else -> R.drawable.ic_null_file
                    }
                ),
                contentDescription = "Файл",
                modifier = Modifier.size(50.dp),
                tint = Color.Unspecified
            )
            Spacer(Modifier.width(8.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = file.fileName,
                    color = AppColors.Secondary,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = file.fileSize,
                    color = AppColors.SecondaryTransparent,
                    fontSize = 14.sp
                )
            }
        }
    }
}

fun FileAttachment.isImage(): Boolean {
    val imageExtensions = setOf("png", "jpg", "jpeg")
    return fileName.substringAfterLast('.', "").lowercase() in imageExtensions
}