package com.github.example.repository

import android.annotation.SuppressLint
import androidx.annotation.AnyThread
import androidx.annotation.MainThread
import androidx.paging.*
import androidx.room.withTransaction
import com.github.example.ext.paging.globalPagingConfig
import com.github.example.utils.PAGING_REMOTE_PAGE_SIZE
import com.github.example.entity.Results
import com.github.example.db.UsersDatabase
import com.github.example.entity.IssueReqParam
import com.github.example.entity.Repo
import com.github.example.http.service.ServiceManager
import com.github.example.manager.UserManager
import com.github.example.viewmodel.ReposViewModel
import com.github.example.utils.processApiResponse
import com.github.example.utils.toast
import javax.inject.Inject

@SuppressLint("CheckResult")
class ReposRepository @Inject constructor(
    remote: RemoteReposDataSource,
    local: LocalReposDataSource
) : com.github.example.base.repository.BaseRepositoryBoth<RemoteReposDataSource, LocalReposDataSource>(remote, local) {

    var sortKeyProvider: () -> String = { ReposViewModel.sortByUpdate }

    @MainThread
    fun fetchRepoPager(): Pager<Int, Repo> {
        val remoteMediator = RepoPageRemoteMediator(remoteDataSource, localDataSource, sortKeyProvider)

        return Pager(
                config = globalPagingConfig,
                remoteMediator = remoteMediator,
                pagingSourceFactory = { localDataSource.fetchRepoPagingSource() }
        )
    }

    suspend fun createIssue(title: String, body: String, owner: String, repo: String) {
        // 如果登录失败，清除登录信息
        when (remoteDataSource.createIssue(title, body, owner, repo)) {
            is Results.Failure -> toast("commit issue failed")
            is Results.Success -> toast("commit issue successfully")
        }
    }

}

class RemoteReposDataSource @Inject constructor(private val serviceManager: ServiceManager) :
    com.github.example.base.repository.IRemoteDataSource {

    suspend fun queryRepos(
            username: String,
            pageIndex: Int,
            perPage: Int,
            sort: String
    ): List<Repo> {
        return serviceManager.userService.queryRepos(username, pageIndex, perPage, sort)
    }

    suspend fun createIssue(title: String, body: String, owner: String, repo: String): Results<Repo> {
        return processApiResponse{
            serviceManager.userService.createIssue(body = IssueReqParam(title, body), owner = owner, repo = repo)
        }
    }
}

class LocalReposDataSource @Inject constructor(
        private val db: UsersDatabase
) : com.github.example.base.repository.ILocalDataSource {

    @AnyThread
    fun fetchRepoPagingSource(): PagingSource<Int, Repo> {
        return db.userReposDao().queryRepos()
    }

    @AnyThread
    suspend fun clearOldAndInsertNewData(newPage: List<Repo>) {
        db.withTransaction {
            db.userReposDao().deleteAllRepos()
            insertDataInternal(newPage)
        }
    }

    @AnyThread
    suspend fun insertNewPageData(newPage: List<Repo>) {
        db.withTransaction { insertDataInternal(newPage) }
    }

    @AnyThread
    suspend fun fetchNextIndexInRepos(): Int {
        return db.withTransaction {
            db.userReposDao().getNextIndexInRepos() ?: 0
        }
    }

    @AnyThread
    private suspend fun insertDataInternal(newPage: List<Repo>) {
        val start = fetchNextIndexInRepos()
        val items = newPage.mapIndexed { index, child ->
            child.indexInSortResponse = start + index
            child
        }
        db.userReposDao().insert(items)
    }
}

@OptIn(ExperimentalPagingApi::class)
class RepoPageRemoteMediator(
    private val remoteDataSource: RemoteReposDataSource,
    private val localDataSource: LocalReposDataSource,
    private val sortKeyProvider: () -> String
) : RemoteMediator<Int, Repo>() {

    override suspend fun load(loadType: LoadType, state: PagingState<Int, Repo>): MediatorResult {
        return try {
            val pageIndex = when (loadType) {
                LoadType.REFRESH -> 1
                LoadType.PREPEND -> return MediatorResult.Success(true)
                LoadType.APPEND -> {
                    val nextIndex = localDataSource.fetchNextIndexInRepos()
                    if (nextIndex % PAGING_REMOTE_PAGE_SIZE != 0) {
                        return MediatorResult.Success(true)
                    }
                    nextIndex / PAGING_REMOTE_PAGE_SIZE + 1
                }
            }
            val sortKey = sortKeyProvider()
            val username = UserManager.INSTANCE?.login ?: ""
            val data = remoteDataSource.queryRepos(username, pageIndex, PAGING_REMOTE_PAGE_SIZE, sortKey)
            if (loadType == LoadType.REFRESH) {
                localDataSource.clearOldAndInsertNewData(data)
            } else {
                localDataSource.insertNewPageData(data)
            }
            MediatorResult.Success(data.isEmpty())
        } catch (exception: Exception) {
            toast(exception.toString())
            MediatorResult.Error(exception)
        }
    }
}
