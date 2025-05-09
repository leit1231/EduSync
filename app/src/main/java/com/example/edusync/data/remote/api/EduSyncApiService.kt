package com.example.edusync.data.remote.api

import com.example.edusync.data.remote.dto.AuthResponse
import com.example.edusync.data.remote.dto.ChatResponse
import com.example.edusync.data.remote.dto.ChatUser
import com.example.edusync.data.remote.dto.CreateChatRequest
import com.example.edusync.data.remote.dto.CreateChatResponse
import com.example.edusync.data.remote.dto.CreatePollRequest
import com.example.edusync.data.remote.dto.EditMessageResponse
import com.example.edusync.data.remote.dto.FavoriteFileDto
import com.example.edusync.data.remote.dto.FileDto
import com.example.edusync.data.remote.dto.GroupResponse
import com.example.edusync.data.remote.dto.InstituteResponse
import com.example.edusync.data.remote.dto.JoinByInviteRequest
import com.example.edusync.data.remote.dto.LoginRequest
import com.example.edusync.data.remote.dto.MessageDto
import com.example.edusync.data.remote.dto.MessageResponse
import com.example.edusync.data.remote.dto.PollDto
import com.example.edusync.data.remote.dto.PollResponse
import com.example.edusync.data.remote.dto.RefreshInviteCodeResponse
import com.example.edusync.data.remote.dto.RefreshRequest
import com.example.edusync.data.remote.dto.RegisterRequest
import com.example.edusync.data.remote.dto.ScheduleItem
import com.example.edusync.data.remote.dto.ScheduleUpdateRequest
import com.example.edusync.data.remote.dto.SubjectResponse
import com.example.edusync.data.remote.dto.TeacherInitialsResponse
import com.example.edusync.data.remote.dto.UpdateProfileRequest
import com.example.edusync.data.remote.dto.UserProfileResponse
import com.example.edusync.data.remote.dto.VoteRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.HTTP
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
    @GET("/api/chats")
    suspend fun getChats(@Header("Authorization") token: String): Response<List<ChatResponse>>

    @POST("/api/chats")
    suspend fun createChat(
        @Header("Authorization") token: String,
        @Body body: CreateChatRequest
    ): Response<CreateChatResponse>

    @DELETE("/api/chats/{id}")
    suspend fun deleteChat(@Header("Authorization") token: String, @Path("id") chatId: Int): Response<Unit>

    @PUT("/api/chats/{id}/invite")
    suspend fun refreshInviteCode(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int
    ): Response<RefreshInviteCodeResponse>

    @POST("/api/chats/join")
    suspend fun joinChatByInvite(@Header("Authorization") token: String, @Body body: JoinByInviteRequest): Response<Unit>

    @DELETE("/api/chats/{id}/leave")
    suspend fun leaveChat(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int
    ): Response<Unit>

    @GET("/api/chats/{id}/participants")
    suspend fun getChatParticipants(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int
    ): Response<List<ChatUser>>

    @DELETE("/api/chats/{id}/participants/{userId}")
    suspend fun removeChatParticipant(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Path("userId") userId: Int
    ): Response<Unit>
    //Сообщения
    @Multipart
    @POST("/api/chats/{id}/messages")
    suspend fun sendMessage(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Part("text") text: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<MessageResponse>

    @GET("/api/chats/{id}/messages")
    suspend fun getMessages(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<List<MessageDto>>

    @Multipart
    @POST("/api/chats/{id}/messages/{messageID}/reply")
    suspend fun replyMessage(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Path("messageID") messageId: Int,
        @Part("text") text: RequestBody,
        @Part files: List<MultipartBody.Part>
    ): Response<MessageResponse>

    @DELETE("/api/chats/{id}/messages/{messageID}")
    suspend fun deleteMessage(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Path("messageID") messageId: Int
    ): Response<MessageResponse>

    @GET("/api/chats/{id}/messages/search")
    suspend fun searchMessages(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Query("query") query: String,
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<List<MessageDto>>

    @PATCH("/api/chats/{id}/messages/{message_id}")
    suspend fun editMessage(
        @Header("Authorization") token: String,
        @Path("id") chatId: Int,
        @Path("message_id") messageId: Int,
        @Body body: Map<String, String>
    ): Response<EditMessageResponse>

    //Файл
    @GET("/api/files/{id}")
    suspend fun downloadFileById(
        @Header("Authorization") token: String,
        @Path("id") fileId: Int
    ): Response<ResponseBody>

    //Избранное
    @GET("/api/files/favorites")
    suspend fun getFavorites(
        @Header("Authorization") token: String
    ): Response<List<FavoriteFileDto>>

    @POST("/api/files/{id}/favorites")
    suspend fun addToFavorites(
        @Header("Authorization") token: String,
        @Path("id") fileId: Int
    ): Response<Unit>

    @DELETE("/api/files/{id}/favorites")
    suspend fun removeFromFavorites(
        @Header("Authorization") token: String,
        @Path("id") fileId: Int
    ): Response<Unit>

    //Опросы
    @GET("/api/chats/{chat_Id}/polls")
    suspend fun getPolls(
        @Header("Authorization") token: String,
        @Path("chat_Id") chatId: Int
    ): Response<List<PollDto>>

    @POST("/api/chats/{chat_Id}/polls")
    suspend fun createPoll(
        @Header("Authorization") token: String,
        @Path("chat_Id") chatId: Int,
        @Body request: CreatePollRequest
    ): Response<PollResponse>

    @POST("/api/chats/{chatId}/polls/{pollId}/vote")
    suspend fun votePollOption(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int,
        @Body vote: VoteRequest
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "/api/chats/{chatId}/polls/{pollId}/vote", hasBody = true)
    suspend fun unvotePoll(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int,
        @Body body: VoteRequest
    ): Response<Unit>

    @DELETE("/api/chats/{chatId}/polls/{pollId}")
    suspend fun deletePoll(
        @Header("Authorization") token: String,
        @Path("chatId") chatId: Int,
        @Path("pollId") pollId: Int
    ): Response<Unit>
}