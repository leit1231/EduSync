package com.example.edusync.presentation.viewModels.favorite

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.edusync.common.Resource
import com.example.edusync.domain.model.favorietesFiles.FileItem
import com.example.edusync.domain.model.message.FileAttachment
import com.example.edusync.domain.use_case.favorite.GetFavoritesUseCase
import com.example.edusync.domain.use_case.favorite.RemoveFromFavoritesUseCase
import com.example.edusync.domain.use_case.file.GetFileByIdUseCase
import com.example.edusync.presentation.navigation.Destination
import com.example.edusync.presentation.navigation.Navigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.File

class FavoritesViewModel(
    private val navigator: Navigator,
    private val getFavoritesUseCase: GetFavoritesUseCase,
    private val getFileByIdUseCase: GetFileByIdUseCase,
    private val removeFromFavoritesUseCase: RemoveFromFavoritesUseCase
) : ViewModel(){

    private val _favoriteFiles = MutableStateFlow<List<FileAttachment>>(emptyList())

    private val _displayedFiles = MutableStateFlow<List<FileItem>>(emptyList())
    val displayedFiles: StateFlow<List<FileItem>> = _displayedFiles

    private var allFiles = listOf<FileItem>()

    private var currentQuery: String = ""

    fun filterFavorites(query: String) {
        currentQuery = query
        _displayedFiles.value = allFiles.filter {
            it.name.contains(query, ignoreCase = true)
        }
    }

    fun toggleFavorite(file: FileItem, context: Context) {
        viewModelScope.launch {
            removeFromFavoritesUseCase(file.id).collect { result ->
                if (result is Resource.Success) {
                    _favoriteFiles.update { it.filterNot { it.id == file.id } }
                    allFiles = allFiles.filterNot { it.id == file.id }
                    _displayedFiles.value = allFiles
                } else if (result is Resource.Error) {
                    Toast.makeText(context, "Ошибка при удалении из избранного", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun toggleDownload(file: FileItem, context: Context) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val targetFile = File(downloadsDir, "${file.id}-${file.name}")

        if (targetFile.exists()) {
            val deleted = targetFile.delete()
            if (deleted) {
                _displayedFiles.update { files ->
                    files.map {
                        if (it.id == file.id) it.copy(isDownload = false) else it
                    }
                }
            }
        } else {
            viewModelScope.launch {
                getFileByIdUseCase(file.id, null).collect { result ->
                    if (result is Resource.Success) {
                        val (body, _) = result.data ?: return@collect
                        saveToDownloads(body, targetFile)
                        _displayedFiles.update { files ->
                            files.map {
                                if (it.id == file.id) it.copy(isDownload = true) else it
                            }
                        }
                    }
                }
            }
        }
    }

    private fun saveToDownloads(body: ResponseBody, file: File) {
        body.byteStream().use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
    }

    fun loadFavorites(context: Context) {
        viewModelScope.launch {
            getFavoritesUseCase().collect { result ->
                if (result is Resource.Success) {
                    val files = result.data.orEmpty()
                    val attachments = mutableListOf<FileAttachment>()
                    val fileItems = mutableListOf<FileItem>()

                    files.forEach { fav ->
                        getFileByIdUseCase(fav.id, fav.file_url).collect { fileRes ->
                            if (fileRes is Resource.Success) {
                                val (body, _) = fileRes.data ?: return@collect
                                val fileName = extractFileName(fav.file_url)

                                val cachedFile = File(context.cacheDir, "${fav.id}-$fileName")
                                if (!cachedFile.exists()) {
                                    saveFileToCache(body, cachedFile)
                                }

                                val downloadsFile = File(
                                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                                    "${fav.id}-$fileName"
                                )

                                val attachment = FileAttachment(
                                    id = fav.id,
                                    uri = FileProvider.getUriForFile(
                                        context,
                                        context.packageName + ".provider",
                                        cachedFile
                                    ),
                                    fileName = fileName,
                                    fileSize = formatSize(cachedFile.length())
                                )

                                attachments.add(attachment)

                                fileItems.add(
                                    FileItem(
                                        id = fav.id,
                                        name = fileName,
                                        size = formatSize(cachedFile.length()),
                                        uri = attachment.uri,
                                        isFavorite = true,
                                        isDownload = downloadsFile.exists()
                                    )
                                )
                            }
                        }
                    }

                    _favoriteFiles.value = attachments
                    allFiles = fileItems
                    _displayedFiles.value = allFiles
                    filterFavorites(currentQuery)
                }
            }
        }
    }

    private fun saveFileToCache(body: ResponseBody, file: File) {
        body.byteStream().use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
    }

    private fun extractFileName(url: String): String {
        val raw = url.substringAfterLast("/")
        return raw.substringAfter("_")
    }

    fun openFile(file: FileItem, context: Context) {
        val uri = file.uri
        val extension = file.name.substringAfterLast('.', "").lowercase()

        viewModelScope.launch {
            if (extension == "pdf") {
                navigator.navigate(
                    destination = Destination.PdfScreenDestination(uri = uri.toString())
                )
            } else {
                try {
                    val intent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, getMimeType(uri, context))
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    Toast.makeText(context, "Ошибка открытия файла", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun formatSize(size: Long): String = when {
        size < 1024 -> "$size Б"
        size < 1024 * 1024 -> String.format("%.1f КБ", size / 1024.0)
        else -> String.format("%.1f МБ", size / (1024.0 * 1024.0))
    }

    private fun getMimeType(uri: Uri, context: Context): String {
        return if (uri.scheme == "content") {
            context.contentResolver.getType(uri) ?: "*/*"
        } else {
            when (uri.toString().substringAfterLast('.', "").lowercase()) {
                "pdf" -> "application/pdf"
                "doc", "docx" -> "application/msword"
                "xls", "xlsx" -> "application/vnd.ms-excel"
                "txt" -> "text/plain"
                else -> "*/*"
            }
        }
    }
}