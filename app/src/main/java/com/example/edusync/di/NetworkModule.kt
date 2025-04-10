package com.example.edusync.di

import com.example.edusync.common.Constants
import com.example.edusync.data.remote.api.EduSyncApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        val client = OkHttpClient().newBuilder()
            .addInterceptor(HttpLoggingInterceptor { message -> println("REQUEST: $message") }
                .apply { setLevel(HttpLoggingInterceptor.Level.BODY) })
            .build()

        Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(EduSyncApiService::class.java)
    }
}