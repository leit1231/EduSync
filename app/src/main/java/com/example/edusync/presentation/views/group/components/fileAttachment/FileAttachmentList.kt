package com.example.edusync.presentation.views.group.components.fileAttachment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.edusync.presentation.viewModels.group.FileAttachment

@Composable
fun FileAttachmentList(
    files: List<FileAttachment>,
    onRemoveFile: (FileAttachment) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(vertical = 8.dp)
    ) {
        items(files) { file ->
            FileAttachmentView(file) { onRemoveFile(file) }
        }
    }
}