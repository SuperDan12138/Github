package com.github.example.repository

import androidx.paging.Pager
import androidx.paging.PagingSource
import com.github.example.entity.Repo
import com.github.example.ext.paging.globalPagingConfig
import com.github.example.http.service.UserService
import com.github.example.utils.PAGING_REMOTE_PAGE_SIZE
import com.github.example.viewmodel.SearchViewModel
import javax.inject.Inject

class SearchRepository @Inject constructor(
    remoteDataSource: SearchRemoteDataSource
) : com.github.example.base.repository.BaseRepositoryRemote<SearchRemoteDataSource>(remoteDataSource) {

    var sortKeyProvider: () -> String = { SearchViewModel.sortByUpdate }

    fun fetchPager(keyWord: String): Pager<Int, Repo> {
        return remoteDataSource.getPager(keyWord, sortKeyProvider)
    }
}

class SearchRemoteDataSource @Inject constructor(
    private val userService: UserService
) : com.github.example.base.repository.IRemoteDataSource {

    fun getPager(keyWord: String, sortKeyProvider: () -> String): Pager<Int, Repo> {
        return Pager(
            config = globalPagingConfig,
            pagingSourceFactory = { SearchPagingSource(userService, keyWord, sortKeyProvider) }
        )
    }
}

class SearchPagingSource(
    private val userService: UserService,
    private val keyWord: String,
    private val sortKeyProvider: () -> String
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
            val searchResult = userService.search(
                keyWord, key, PAGING_REMOTE_PAGE_SIZE,
                sortKeyProvider()
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
}
