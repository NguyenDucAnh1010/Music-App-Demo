package com.ducanh.musicappdemo.di

import android.content.Context
import android.util.Log
import androidx.room.Room
import com.ducanh.musicappdemo.data.api.ApiService
import com.ducanh.musicappdemo.data.dao.SongDao
import com.ducanh.musicappdemo.data.database.SongDatabase
import com.ducanh.musicappdemo.presentation.repository.SongFavoriteRepository
import com.ducanh.musicappdemo.presentation.repository.SongFavoriteRepositoryImpl
import com.ducanh.musicappdemo.presentation.repository.SongOfflineRepository
import com.ducanh.musicappdemo.presentation.repository.SongOfflineRepositoryImpl
import com.ducanh.musicappdemo.presentation.repository.SongOnlineRepository
import com.ducanh.musicappdemo.presentation.repository.SongOnlineRepositoryImpl
import com.ducanh.musicappdemo.ui.viewmodel.MusicViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    val baseUrl = "https://m.vuiz.net/getlink/mp3zing/"

    @Provides
    @Singleton
    fun provideSongOnlineRepository(api: ApiService): SongOnlineRepository {
        return SongOnlineRepositoryImpl(api)
    }

    @Provides
    @Singleton
    fun provideSongOfflineRepository(@ApplicationContext context: Context): SongOfflineRepository {
        return SongOfflineRepositoryImpl(context)
    }

    @Provides
    @Singleton
    fun provideSongFavoriteRepository(songDao: SongDao): SongFavoriteRepository {
        return SongFavoriteRepositoryImpl(songDao)
    }

    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private const val TAG = "AppModule"

    @Provides
    @Singleton
    fun provideWordDatabase(@ApplicationContext context: Context): SongDatabase {
        println("Starting to create database from assets...")
        Log.d(TAG, "Starting to create database from assets...")

        val database = Room.databaseBuilder(
            context,
            SongDatabase::class.java,
            SongDatabase.DATABASE_NAME
        ).allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()

        val isOpen = database.isOpen
        println("Database created. Is open: $isOpen")
        Log.d(TAG, "Database created. Is open: $isOpen")

        return database
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideWordDao(songDatabase: SongDatabase): SongDao {
        return songDatabase.songDao()
    }

    @Provides
    @Singleton
    fun provideMusicViewModel(): MusicViewModel {
        return MusicViewModel()
    }
}