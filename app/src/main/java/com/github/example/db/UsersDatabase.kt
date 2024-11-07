package com.github.example.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.example.entity.Repo

@Database(
        entities = [Repo::class],
        version = 1
)
abstract class UsersDatabase : RoomDatabase() {

    abstract fun userReposDao(): UserReposDao
}