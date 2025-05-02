package com.example.edusync.domain.repository.file

import com.example.edusync.data.remote.dto.FileDto

interface FileRepository {
    suspend fun getFileById(fileId: Int): Result<FileDto>
}
