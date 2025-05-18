package com.example.edusync.presentation.views.group.components.fileAttachment

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.eduHub.edusync.R
import com.example.edusync.domain.model.message.FileAttachment
import com.example.edusync.presentation.theme.ui.AppColors

@Composable
fun FileAttachmentView(file: FileAttachment, onRemove: (() -> Unit)? = null) {

    val extension = file.fileName.substringAfterLast('.', "").lowercase()
    val isImage = extension in setOf("png", "jpg", "jpeg")

    Column(
        modifier = Modifier
            .width(90.dp)
            .background(AppColors.OnBackground, RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {

        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = "Удалить",
            tint = AppColors.Primary,
            modifier = Modifier
                .size(20.dp)
                .align(Alignment.End)
                .clickable { onRemove?.invoke() }
        )

        if (isImage) {
            AsyncImage(
                model = file.uri,
                contentDescription = "Изображение",
                modifier = Modifier
                    .size(50.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
                error = painterResource(R.drawable.ic_jpg)
            )
        } else {
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
        }

        Text(
            text = file.fileName,
            color = AppColors.Secondary,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
        )

        Text(
            text = file.fileSize,
            color = AppColors.Secondary.copy(alpha = 0.7f),
            fontSize = 12.sp,
        )
    }
}