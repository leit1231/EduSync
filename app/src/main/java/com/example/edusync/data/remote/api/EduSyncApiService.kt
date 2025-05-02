package com.example.edusync.data.remote.api

import com.example.edusync.data.remote.dto.AddUserToChatRequest
import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.CreatePollRequest
import com.example.edusync.data.remote.dto.FavoriteFileDto
import com.example.edusync.data.remote.dto.FileDto
import com.example.edusync.data.remote.dto.GroupResponse
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.data.remote.dto.InviteToChatRequest
import com.example.edusync.data.remote.dto.JoinByInviteRequest
import com.example.edusync.data.remote.dto.LeaveChatRequest
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.dto.PollDto
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.data.remote.dto.ScheduleItem
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.SubjectResponse
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.data.remote.dto.UserProfileResponse
import com.example.edusync.data.remote.dto.VoteRequest
import com.example.edusync.domain.model.account.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
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

    //Профиль
    @GET("/api/profile")
    suspend fun profile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @PUT("/api/profile")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: UpdateProfileRequest
    ): Response<AuthResponse>

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

    //Расписание
    @GET("/api/schedule")
    suspend fun getScheduleByGroup(
        @Header("Authorization") token: String,
        @Query("group_id") groupId: Int
    ): Response<List<ScheduleItem>>

    @PATCH("/api/schedule/{id}")
    suspend fun updateSchedule(
        @Header("Authorization") token: String,
        @Path("id") scheduleId: Int,
        @Body body: ScheduleUpdateRequest
    ): Response<Unit>

    @POST("/api/schedule")
    suspend fun createSchedule(
        @Header("Authorization") token: String,
        @Body request: ScheduleUpdateRequest
    ): Response<Unit>

    @DELETE("/api/schedule/{id}")
    suspend fun deleteSchedule(
        @Header("Authorization") token: String,
        @Path("id") scheduleId: Int
    ): Response<Unit>

    //Получение списка преподавателей
    @GET("/api/schedule/initials")
    suspend fun getTeacherInitials(
        @Header("Authorization") token: String
    ): Response<List<TeacherInitialsResponse>>

    @GET("/api/schedule/teacher_initials/{initials_id}")
    suspend fun getScheduleByTeacher(
        @Header("Authorization") token: String,
        @Path("initials_id") initialsId: Int
    ): Response<List<ScheduleItem>>

    // Получение списка предметов группы
    @GET("/api/subject/group/{groupId}")
    suspend fun getSubjectsByGroup(
        @Header("Authorization") token: String,
        @Path("groupId") groupId: Int
    ): Response<List<SubjectResponse>>

    //Чаты
    @GET("/api/chat")
    suspend fun getChats(@Header("Authorization") token: String): Response<List<ChatResponse>>

    @POST("/api/chat")
    suspend fun createChat(@Header("Authorization") token: String, @Body body: CreateChatRequest): Response<Unit>

    @DELETE("/api/chat/{id}")
    suspend fun deleteChat(@Header("Authorization") token: String, @Path("id") chatId: Int): Response<Unit>

    @PATCH("/api/chat/{id}/add_user")
    suspend fun addUserToChat(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Body body: AddUserToChatRequest
    ): Response<Unit>

    @POST("/api/chat/invite")
    suspend fun inviteToChat(@Header("Authorization") token: String, @Body body: InviteToChatRequest): Response<Unit>

    @POST("/api/chat/join")
    suspend fun joinChatByInvite(@Header("Authorization") token: String, @Body body: JoinByInviteRequest): Response<Unit>

    @POST("/api/chat/leave")
    suspend fun leaveChat(@Header("Authorization") token: String, @Body body: LeaveChatRequest): Response<Unit>

    @PUT("/api/chats/{id}/invite")
    suspend fun refreshInviteCode(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int
    ): Response<Unit>

    @GET("/api/chats/{id}/participants")
    suspend fun getChatParticipants(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int
    ): Response<List<User>>

    @DELETE("/api/chats/{id}/participants/{userId}")
    suspend fun removeChatParticipant(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>

    //Сообщения
    @Multipart
    @POST("/api/chats/{chatId}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Part("text") text: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<Unit>

    @GET("/api/chats/{chatId}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int
    ): Response<List<MessageDto>>

    @POST("/api/chats/{chatId}/messages/{messageId}/reply")
    suspend fun replyMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("messageId") messageId: Int,
        @Body body: Map<String, String>
    ): Response<Unit>

    @DELETE("/api/chats/{chatId}/messages/{messageId}")
    suspend fun deleteMessage(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("messageId") messageId: Int
    ): Response<Unit>

    @GET("/api/chats/{chatId}/messages/search")
    suspend fun searchMessages(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<List<MessageDto>>

    //Файл
    @GET("/api/files/{id}")
    suspend fun getFileById(
        @Header("Authorization") token: String,
        @Path("id") fileId: Int
    ): Response<FileDto>

    //Избранное
    @GET("/api/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): Response<List<FavoriteFileDto>>

    @POST("/api/favorites/{fileId}")
    suspend fun addToFavorites(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: Int
    ): Response<Unit>

    @DELETE("/api/favorites/{fileId}")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String,
        @Path("fileId") fileId: Int
    ): Response<Unit>

    //Опросы
    @GET("/api/chats/{chatId}/polls")
    suspend fun getPolls(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int
    ): Response<List<PollDto>>

    @POST("/api/chats/{chatId}/polls")
    suspend fun createPoll(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Body request: CreatePollRequest
    ): Response<Unit>

    @POST("/api/chats/{chatId}/polls/{pollId}/vote")
    suspend fun votePollOption(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int,
        @Body vote: VoteRequest
    ): Response<Unit>

    @DELETE("/api/chats/{chatId}/polls/{pollId}/unvote")
    suspend fun unvotePoll(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int
    ): Response<Unit>

    @DELETE("/api/chats/{chatId}/polls/{pollId}")
    suspend fun deletePoll(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int
    ): Response<Unit>

}