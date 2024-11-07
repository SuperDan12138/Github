package com.github.example.db

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.github.example.entity.Repo

@Dao
interface UserReposDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(repos: List<Repo>)

    @Query("SELECT * FROM user_repos ORDER BY indexInSortResponse ASC")
    fun queryRepos(): PagingSource<Int, Repo>

    @Query("DELETE FROM user_repos")
    suspend fun deleteAllRepos()

    @Query("SELECT MAX(indexInSortResponse) + 1 FROM user_repos")
    suspend fun getNextIndexInRepos(): Int?
}
