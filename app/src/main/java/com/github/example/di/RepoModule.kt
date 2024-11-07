package com.github.example.di

import android.app.Application
import androidx.datastore.DataStore
import androidx.datastore.createDataStore
import com.github.example.UserPreferencesProtos
import com.github.example.UserPreferencesSerializer
import com.github.example.repository.UserInfoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    @Singleton
    fun provideUserInfoRepository(userDataStore: DataStore<UserPreferencesProtos.UserPreferences>): UserInfoRepository {
        return UserInfoRepository.getInstance(userDataStore)
    }

    @Provides
    @Singleton
    fun providePreferencesDataStore(application: Application): DataStore<UserPreferencesProtos.UserPreferences> {
        return application.createDataStore(
                fileName = "user_prefs.pb",
                serializer = UserPreferencesSerializer
        )
    }
}
