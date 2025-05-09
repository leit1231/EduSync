package com.example.edusync.presentation.views.group.components.pagging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.domain.repository.message.MessageRepository

class MessagePagingSource(
    private val chatId: Int,
    private val query: String?,
    private val repository: MessageRepository
) : PagingSource<Int, MessageDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MessageDto> {
        val offset = params.key ?: 0
        val limit = params.loadSize

        return try {
            val result = if (query != null) {
                repository.searchMessages(chatId, query, limit, offset)
            } else {
                repository.getMessages(chatId, limit, offset)
            }

            result.fold(
                onSuccess = { messages ->
                    val nextKey = if (messages.size < limit) null else offset + limit
                    LoadResult.Page(
                        data = messages,
                        prevKey = if (offset == 0) null else offset - limit,
                        nextKey = nextKey
                    )
                },
                onFailure = { LoadResult.Error(it) }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, MessageDto>): Int = 0
}
