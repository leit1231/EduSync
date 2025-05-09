package com.example.edusync.common

import android.content.Context
import com.example.edusync.data.local.EncryptedSharedPreference

object Constants {
    const val BASE_URL = "http://192.168.0.28:8080/"

    fun getIsTeacher(context: Context): Boolean {
        val user = EncryptedSharedPreference(context).getUser()
        return user?.isTeacher ?: false
    }
}