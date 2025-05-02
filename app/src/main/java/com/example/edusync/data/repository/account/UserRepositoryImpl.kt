package com.example.edusync.data.repository.account

import android.util.Log
import com.example.edusync.data.local.EncryptedSharedPreference
import com.example.edusync.data.remote.api.EduSyncApiService
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.data.repository.TokenRequestExecutor
import com.example.edusync.domain.model.account.User
import com.example.edusync.domain.repository.account.UserRepository
import retrofit2.Response

class UserRepositoryImpl(
    private val api: EduSyncApiService,
    private val prefs: EncryptedSharedPreference
) : UserRepository {

    private val executor = TokenRequestExecutor(prefs, api)

    override suspend fun register(request: RegisterRequest): Result<Unit> =
        safeApiCall { api.register(request) }.map { }

    override suspend fun login(email: String, password: String): Result<AuthResponse> =
        safeApiCall { api.login(LoginRequest(email, password)) }
            .onSuccess {
                prefs.saveAccessToken(it.access_token)
                prefs.saveRefreshToken(it.refresh_token)
                Log.d("Refresh_token", it.refresh_token)
            }

    override suspend fun logout(): Result<Unit> = executor.execute { api.logout(it) }.also {
        if (it.isSuccess) prefs.clearUserData()
    }

    override suspend fun refresh(refreshToken: String): Result<AuthResponse> =
        safeApiCall { api.refresh(RefreshRequest(refreshToken)) }
            .onSuccess {
                prefs.saveAccessToken(it.access_token)
                prefs.saveRefreshToken(it.refresh_token)
            }

    override suspend fun getProfile(): Result<User> = executor.execute { api.profile(it) }.map {
        User(
            id = it.user_id,
            email = it.email,
            fullName = it.full_name,
            isTeacher = it.is_teacher,
            institutionId = it.institution_id,
            groupId = it.group_id
        ).also { user -> prefs.saveUser(user) }
    }

    override suspend fun updateProfile(request: UpdateProfileRequest): Result<AuthResponse> =
        executor.execute { api.updateProfile(it, request) }.onSuccess {
            prefs.saveAccessToken(it.access_token)
            prefs.saveRefreshToken(it.refresh_token)
        }

    private inline fun <T> safeApiCall(call: () -> Response<T>): Result<T> =
        try {
            val response = call()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Throwable("Empty body"))
            } else {
                Result.failure(Throwable("HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
}