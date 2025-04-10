package com.example.edusync.domain.model.institution

data class Institute(
    val id: Int,
    val name: String
) {
    fun getDisplayName(): String {
        return when (name) {
            "rk" -> "РКСИ"
            else -> name
        }
    }
}