package com.github.example.repository

import androidx.paging.Pager
import androidx.paging.PagingSource
import com.github.example.entity.Repo
import com.github.example.ext.paging.globalPagingConfig
import com.github.example.http.service.UserService
import com.github.example.utils.PAGING_REMOTE_PAGE_SIZE
import javax.inject.Inject

class HomeRepository @Inject constructor(
    remoteDataSource: HomeRemoteDataSource
) : com.github.example.base.repository.BaseRepositoryRemote<HomeRemoteDataSource>(remoteDataSource) {

    fun fetchPager(): Pager<Int, Repo> {
        return remoteDataSource.getPager()
    }
}

class HomeRemoteDataSource @Inject constructor(private val userService: UserService) :
    com.github.example.base.repository.IRemoteDataSource {
    fun getPager(): Pager<Int, Repo> {
        return Pager(
            config = globalPagingConfig,
            pagingSourceFactory = { HomePagingSource(userService) }
        )
    }
}


class HomePagingSource(
    private val userService: UserService
) : PagingSource<Int, Repo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Repo> {
        if (params is LoadParams.Prepend) {
            return LoadResult.Page(
                data = listOf(),
                prevKey = null,
                nextKey = null
            )
        }
        return try {
            val key = if (params.key == null) 1 else params.key as Int
            val searchResult =
                userService.search(
                    SEARCH_KEY_DEFAULT,
                    key,
                    PAGING_REMOTE_PAGE_SIZE,
                    sort = ""
                )
            LoadResult.Page(
                data = searchResult.items,
                prevKey = key - 1,
                nextKey = key + 1
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }

    companion object {
        private const val SEARCH_KEY_DEFAULT = "kotlin"
    }
}
