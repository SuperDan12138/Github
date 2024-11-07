package com.github.example.base.repository

open class BaseRepositoryBoth<T : com.github.example.base.repository.IRemoteDataSource, R : com.github.example.base.repository.ILocalDataSource>(
        val remoteDataSource: T,
        val localDataSource: R
) : com.github.example.base.repository.IRepository

open class BaseRepositoryLocal<T : com.github.example.base.repository.ILocalDataSource>(
        val remoteDataSource: T
) : com.github.example.base.repository.IRepository

open class BaseRepositoryRemote<T : com.github.example.base.repository.IRemoteDataSource>(
        val remoteDataSource: T
) : com.github.example.base.repository.IRepository

open class BaseRepositoryNothing() : com.github.example.base.repository.IRepository