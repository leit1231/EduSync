package com.example.edusync.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.edusync.domain.model.account.User
import com.example.edusync.domain.model.chats.ChatInfo
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class EncryptedSharedPreference(context: Context) {

    private val sharedPreferences = try {
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    } catch (e: Exception) {
        e.printStackTrace()
        val prefs = context.getSharedPreferences("secure_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
        EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun isFirstLaunch(): Boolean {
        return sharedPreferences.getBoolean("is_first_launch", true)
    }

    fun setFirstLaunch(isFirstLaunch: Boolean) {
        sharedPreferences.edit().putBoolean("is_first_launch", isFirstLaunch).apply()
    }

    fun saveAccessToken(token: String) {
        sharedPreferences.edit().putString("access_token", token).apply()
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("access_token", null)
    }

    fun saveRefreshToken(token: String) {
        sharedPreferences.edit().putString("refresh_token", token).apply()
    }

    fun getRefreshToken(): String? {
        return sharedPreferences.getString("refresh_token", null)
    }

    fun clearUserData() {
        sharedPreferences.edit().apply {
            remove("access_token")
            remove("refresh_token")
            remove("user_data")
            remove("teacher_id")
            apply()
        }
    }

    fun saveUser(user: User) {
        val json = Gson().toJson(user)
        sharedPreferences.edit().putString("user_data", json).apply()
    }

    fun isTeacher(): Boolean {
        return getUser()?.isTeacher ?: false
    }

    fun getUser(): User? {
        val json = sharedPreferences.getString("user_data", null)
        return json?.let { Gson().fromJson(it, User::class.java) }
    }

    fun saveTeacherId(id: Int) =
        sharedPreferences.edit().putInt("teacher_id", id).apply()

    fun getTeacherId(): Int? {
        val id = sharedPreferences.getInt("teacher_id", -1)
        return if (id != -1) id else null
    }

    fun saveChats(chats: List<ChatInfo>) {
        val json = Gson().toJson(chats)
        sharedPreferences.edit()
            .putString("chats_list", json)
            .apply()
    }

    fun getChats(): List<ChatInfo>? {
        val json = sharedPreferences.getString("chats_list", null) ?: return null
        val type = object : TypeToken<List<ChatInfo>>() {}.type
        return Gson().fromJson(json, type)
    }
}