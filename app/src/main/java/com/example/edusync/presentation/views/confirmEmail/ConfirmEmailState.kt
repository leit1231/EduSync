package com.example.edusync.presentation.views.confirmEmail

data class ConfirmEmailState(
    val code: List<Int?> = List(6) { null },
    val focusedIndex: Int? = null,
    val isLoading: Boolean = false
)