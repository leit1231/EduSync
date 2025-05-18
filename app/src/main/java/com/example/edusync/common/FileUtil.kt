package com.example.edusync.common

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source

object FileUtil {
    fun getFileFromUri(context: Context, uri: Uri): MultipartBody.Part {
        val contentResolver = context.contentResolver

        val fileName = contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) cursor.getString(nameIndex) else null
        } ?: "file"

        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        val requestBody = object : RequestBody() {
            override fun contentType(): MediaType? = mimeType.toMediaTypeOrNull()

            override fun writeTo(sink: BufferedSink) {
                contentResolver.openInputStream(uri)?.use { input ->
                    input.source().use { source ->
                        sink.writeAll(source)
                    }
                } ?: throw IllegalStateException("Unable to open input stream for URI: $uri")
            }
        }

        return MultipartBody.Part.createFormData("files", fileName, requestBody)
    }
}