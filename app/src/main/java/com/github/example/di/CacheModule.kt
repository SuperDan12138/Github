package com.github.example.di

import android.app.Application
import androidx.room.Room
import com.github.example.db.UsersDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CacheModule {

    @Provides
    @Singleton
    fun provideUserDatabase(application: Application): UsersDatabase {
        return Room.databaseBuilder(application, UsersDatabase::class.java, "user")
                .fallbackToDestructiveMigration()
                .build()
    }
}
