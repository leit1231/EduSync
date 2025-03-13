package com.example.edusync.presentation.views.enterCode

data class EnterScreenState(
    val code: List<Int?> = List(6) { null },
    val focusedIndex: Int? = null,
    val isLoading: Boolean = false
)