package com.example.edusync.di

import com.example.edusync.data.repository.account.UserRepositoryImpl
import com.example.edusync.data.repository.group.GroupRepositoryImpl
import com.example.edusync.data.repository.institution.InstituteRepositoryImpl
import com.example.edusync.data.repository.schedule.ScheduleRepositoryImpl
import com.example.edusync.domain.repository.account.UserRepository
import com.example.edusync.domain.repository.group.GroupRepository
import com.example.edusync.domain.repository.institution.InstituteRepository
import com.example.edusync.domain.repository.schedule.ScheduleRepository
import com.example.edusync.domain.use_case.account.GetProfileUseCase
import com.example.edusync.domain.use_case.account.LoginUseCase
import com.example.edusync.domain.use_case.account.LogoutUseCase
import com.example.edusync.domain.use_case.account.RefreshTokenUseCase
import com.example.edusync.domain.use_case.account.RegisterUseCase
import com.example.edusync.domain.use_case.group.GetGroupByIdUseCase
import com.example.edusync.domain.use_case.group.GetGroupsByInstitutionIdUseCase
import com.example.edusync.domain.use_case.institution.GetAllInstitutesUseCase
import com.example.edusync.domain.use_case.institution.GetInstituteByIdUseCase
import com.example.edusync.domain.use_case.institution.GetMaskedInstitutesUseCase
import com.example.edusync.domain.use_case.schedule.GetGroupScheduleUseCase
import org.koin.dsl.module

val repositoryModule = module {
    single<UserRepository> { UserRepositoryImpl(get(), get()) }
    single<InstituteRepository> {
        InstituteRepositoryImpl(get(), get())
    }
    single<GroupRepository> {
        GroupRepositoryImpl(get(), get())
    }
    single<ScheduleRepository> { ScheduleRepositoryImpl(get(), get()) }

    factory { RegisterUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { RefreshTokenUseCase(get()) }
    factory { GetProfileUseCase(get()) }

    factory { GetGroupsByInstitutionIdUseCase(get()) }
    factory { GetGroupByIdUseCase(get()) }

    factory { GetAllInstitutesUseCase(get()) }
    factory { GetInstituteByIdUseCase(get()) }
    factory { GetMaskedInstitutesUseCase(get()) }

    factory { GetGroupScheduleUseCase(get()) }
}