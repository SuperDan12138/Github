package com.github.example.entity

import com.github.example.base.viewstate.IViewState

sealed class Results<out T> : IViewState {

    companion object {
        fun <T> success(result: T): Results<T> = Success(result)
        fun <T> failure(error: Throwable): Results<T> = Failure(error)
    }

    data class Failure(val error: Throwable) : Results<Nothing>()
    data class Success<out T>(val data: T) : Results<T>()
}