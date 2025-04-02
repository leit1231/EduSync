package com.example.edusync.presentation.viewModels.favorite

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel

class FavoritesViewModel : ViewModel() {
    val favoriteFiles: List<FileItem>
        get() = _allFiles.filter { it.isFavorite }
    private val _allFiles = mutableStateListOf<FileItem>()
    private val _displayedFiles = mutableStateListOf<FileItem>()
    val displayedFiles: List<FileItem> get() = _displayedFiles

    init {
        loadFiles()
        filterFavorites("")
    }

    fun filterFavorites(query: String) {
        val filtered = _allFiles.filter { file ->
            (file.isFavorite || file.isDownload) &&
                    (query.isEmpty() || file.name.contains(query, ignoreCase = true))
        }
        _displayedFiles.apply {
            clear()
            addAll(filtered)
        }
    }

    fun toggleFavorite(file: FileItem) {
        val index = _allFiles.indexOf(file)
        if (index != -1) {
            _allFiles[index] = file.copy(isFavorite = !file.isFavorite)
        }
    }

    fun toggleDownload(file: FileItem) {
        val index = _allFiles.indexOf(file)
        if (index != -1) {
            _allFiles[index] = file.copy(isDownload = !file.isDownload)
        }
    }

    private fun loadFiles() {
        _allFiles.addAll(
            listOf(
                FileItem(1, "Документ1.pdf", "2MB", true, false),
                FileItem(2, "План урока.docx", "500KB", true, true)
            )
        )
    }
}

data class FileItem(val id: Int, val name: String, val size: String, var isFavorite: Boolean, var isDownload: Boolean)