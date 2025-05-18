package com.example.edusync.common

import android.content.Context
import com.example.edusync.data.local.EncryptedSharedPreference
import ru.eduHub.edusync.BuildConfig

object Constants {
    val BASE_URL = BuildConfig.BASE_URL
    val WS_URL = BuildConfig.WS_URL

    fun getIsTeacher(context: Context): Boolean {
        val user = EncryptedSharedPreference(context).getUser()
        return user?.isTeacher ?: false
    }
}