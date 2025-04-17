package com.example.edusync.data.remote.api

import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.GroupResponse
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.data.remote.dto.ScheduleResponse
import com.example.edusync.data.remote.dto.UserProfileResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface EduSyncApiService {

    // Регистрация, вход
    @POST("/api/register")
    suspend fun register(@Body body: RegisterRequest): Response<Unit>

    @POST("/api/login")
    suspend fun login(@Body body: LoginRequest): Response<AuthResponse>

    @POST("/api/logout")
    suspend fun logout(@Header("Authorization") token: String): Response<Unit>

    @POST("/api/refresh")
    suspend fun refresh(@Body body: RefreshRequest): Response<AuthResponse>

    @GET("/api/profile")
    suspend fun profile(@Header("Authorization") token: String): Response<UserProfileResponse>

    // Получение групп
    @GET("api/group/institution/{id}")
    suspend fun getGroupsByInstitution(
        @Path("id") institutionId: Int
    ): Response<List<GroupResponse>>

    @GET("api/group/{id}")
    suspend fun getGroupById(
        @Path("id") groupId: Int
    ): Response<GroupResponse>

    // Получение институтов
    @GET("api/institution/{id}")
    suspend fun getInstitutionById(@Path("id") id: Int): Response<InstituteResponse>

    @GET("api/institutions")
    suspend fun getAllInstitutions(): Response<List<InstituteResponse>>

    @GET("api/institutions/masked")
    suspend fun getMaskedInstitutions(): Response<List<InstituteResponse>>

    // Получение расписания
    @GET("/api/schedule")
    suspend fun getScheduleByGroup(
        @Query("group_id") groupId: Int
    ): Response<ScheduleResponse>
}