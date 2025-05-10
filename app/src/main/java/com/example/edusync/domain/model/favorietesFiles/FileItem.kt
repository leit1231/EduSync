package com.example.edusync.domain.model.favorietesFiles

import android.net.Uri

data class FileItem(
    val id: Int,
    val name: String,
    val size: String,
    val uri: Uri,
    var isFavorite: Boolean,
    var isDownload: Boolean
)
