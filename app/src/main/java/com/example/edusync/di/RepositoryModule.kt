package com.example.edusync.di

import com.example.edusync.data.repository.account.UserRepositoryImpl
import com.example.edusync.data.repository.chat.ChatRepositoryImpl
import com.example.edusync.domain.use_case.chat.CreateChatUseCase
import com.example.edusync.domain.use_case.chat.DeleteChatUseCase
import com.example.edusync.domain.use_case.chat.GetChatParticipantsUseCase
import com.example.edusync.domain.use_case.chat.JoinChatByInviteUseCase
import com.example.edusync.domain.use_case.chat.LeaveChatUseCase
import com.example.edusync.domain.use_case.chat.RefreshInviteCodeUseCase
import com.example.edusync.domain.use_case.chat.RemoveChatParticipantUseCase
import com.example.edusync.data.repository.favorite.FavoriteRepositoryImpl
import com.example.edusync.data.repository.file.FileRepositoryImpl
import com.example.edusync.data.repository.group.GroupRepositoryImpl
import com.example.edusync.data.repository.institution.InstituteRepositoryImpl
import com.example.edusync.data.repository.message.MessageRepositoryImpl
import com.example.edusync.data.repository.poll.PollRepositoryImpl
import com.example.edusync.data.repository.schedule.ReminderRepository
import com.example.edusync.data.repository.schedule.ScheduleRepositoryImpl
import com.example.edusync.data.repository.subject.SubjectRepositoryImpl
import com.example.edusync.domain.repository.account.UserRepository
import com.example.edusync.domain.repository.chat.ChatRepository
import com.example.edusync.domain.repository.favorite.FavoriteRepository
import com.example.edusync.domain.repository.file.FileRepository
import com.example.edusync.domain.repository.group.GroupRepository
import com.example.edusync.domain.repository.institution.InstituteRepository
import com.example.edusync.domain.repository.message.MessageRepository
import com.example.edusync.domain.repository.poll.PollRepository
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import com.example.edusync.domain.repository.subject.SubjectRepository
import com.example.edusync.domain.use_case.account.GetProfileUseCase
import com.example.edusync.domain.use_case.account.LoginUseCase
import com.example.edusync.domain.use_case.account.LogoutUseCase
import com.example.edusync.domain.use_case.account.RefreshTokenUseCase
import com.example.edusync.domain.use_case.account.RegisterUseCase
import com.example.edusync.domain.use_case.account.UpdateProfileUseCase
import com.example.edusync.domain.use_case.chat.GetChatsUseCase
import com.example.edusync.domain.use_case.favorite.AddToFavoritesUseCase
import com.example.edusync.domain.use_case.favorite.GetFavoritesUseCase
import com.example.edusync.domain.use_case.favorite.RemoveFromFavoritesUseCase
import com.example.edusync.domain.use_case.file.GetFileByIdUseCase
import com.example.edusync.domain.use_case.group.GetGroupByIdUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.institution.GetAllInstitutesUseCase
import com.example.edusync.domain.use_case.institution.GetInstituteByIdUseCase
import com.example.edusync.domain.use_case.institution.GetMaskedInstitutesUseCase
import com.example.edusync.domain.use_case.message.DeleteMessageUseCase
import com.example.edusync.domain.use_case.message.GetMessagesUseCase
import com.example.edusync.domain.use_case.message.ReplyToMessageUseCase
import com.example.edusync.domain.use_case.message.SearchMessagesUseCase
import com.example.edusync.domain.use_case.message.SendMessageUseCase
import com.example.edusync.domain.use_case.poll.CreatePollUseCase
import com.example.edusync.domain.use_case.poll.DeletePollUseCase
import com.example.edusync.domain.use_case.poll.GetPollsUseCase
import com.example.edusync.domain.use_case.poll.UnvotePollUseCase
import com.example.edusync.domain.use_case.poll.VotePollUseCase
import com.example.edusync.domain.use_case.schedule.CreateScheduleUseCase
import com.example.edusync.domain.use_case.schedule.DeleteScheduleUseCase
import com.example.edusync.domain.use_case.schedule.GetGroupScheduleUseCase
import com.example.edusync.domain.use_case.schedule.GetScheduleByTeacherUseCase
import com.example.edusync.domain.use_case.schedule.UpdateScheduleUseCase
import com.example.edusync.domain.use_case.subject.GetSubjectsByGroupUseCase
import com.example.edusync.domain.use_case.teachers.GetTeacherInitialsUseCase
import org.koin.dsl.module

val repositoryModule = module {

    single<UserRepository> { UserRepositoryImpl(get(), get()) }

    single<InstituteRepository> {
        InstituteRepositoryImpl(get(), get())
    }

    single<GroupRepository> {
        GroupRepositoryImpl(get(), get())
    }

    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get(), get(), get(), get()) }
    single<SubjectRepository> { SubjectRepositoryImpl(get(), get()) }
    single<ChatRepository> { ChatRepositoryImpl(get(), get()) }
    single<MessageRepository> { MessageRepositoryImpl(get(), get()) }
    single<FileRepository> { FileRepositoryImpl(get(), get()) }
    single<FavoriteRepository> { FavoriteRepositoryImpl(get(), get()) }
    single<PollRepository> { PollRepositoryImpl(get(), get()) }
    single { ReminderRepository(get()) }

    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { RefreshTokenUseCase(get()) }
    factory { GetProfileUseCase(get()) }
    factory { UpdateProfileUseCase(get()) }

    factory { GetGroupsByInstitutionIdUseCase(get()) }
    factory { GetGroupByIdUseCase(get()) }

    factory { GetAllInstitutesUseCase(get()) }
    factory { GetInstituteByIdUseCase(get()) }
    factory { GetMaskedInstitutesUseCase(get()) }

    factory { GetGroupScheduleUseCase(get()) }
    factory { GetTeacherInitialsUseCase(get()) }
    factory { GetScheduleByTeacherUseCase(get()) }
    factory { UpdateScheduleUseCase(get())}
    factory { DeleteScheduleUseCase(get()) }
    factory { CreateScheduleUseCase(get()) }
    factory { GetSubjectsByGroupUseCase(get()) }

    factory { GetChatsUseCase(get()) }
    factory { CreateChatUseCase(get()) }
    factory { DeleteChatUseCase(get()) }
    factory { GetChatParticipantsUseCase(get()) }
    factory { JoinChatByInviteUseCase(get()) }
    factory { LeaveChatUseCase(get()) }
    factory { RefreshInviteCodeUseCase(get()) }
    factory { RemoveChatParticipantUseCase(get()) }

    factory { SendMessageUseCase(get()) }
    factory { GetMessagesUseCase(get()) }
    factory { ReplyToMessageUseCase(get()) }
    factory { DeleteMessageUseCase(get()) }
    factory { SearchMessagesUseCase(get()) }

    factory { GetFileByIdUseCase(get()) }

    factory { GetFavoritesUseCase(get()) }
    factory { AddToFavoritesUseCase(get()) }
    factory {RemoveFromFavoritesUseCase(get()) }

    factory { CreatePollUseCase(get()) }
    factory { GetPollsUseCase(get()) }
    factory { VotePollUseCase(get()) }
    factory { UnvotePollUseCase(get()) }
    factory { DeletePollUseCase(get()) }
}