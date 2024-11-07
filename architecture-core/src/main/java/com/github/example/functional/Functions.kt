package com.github.example.functional

typealias Supplier<T> = () -> T

interface Consumer<T> {

    fun accept(t: T)
}