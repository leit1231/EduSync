package com.example.edusync.domain.use_case.message

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.repository.message.MessageRepository
import com.example.edusync.presentation.views.group.components.pagging.MessagePagingSource

class MessagePagingUseCase(
    private val repository: MessageRepository
) {
    fun getMessages(chatId: Int, query: String?): Pager<Int, MessageDto> {
        return Pager(
            config = PagingConfig(
                pageSize = 15,
                initialLoadSize = 15,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                MessagePagingSource(chatId, query, repository)
            }
        )
    }
}
