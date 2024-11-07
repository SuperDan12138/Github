package com.github.example.entity

sealed class Error : Throwable() {
    data class NetworkError(val code: Int = -1, val desc: String = "") : Error()
    object UnknownError : Error()
}