package com.github.example.base

import android.app.Application
import android.content.ContextWrapper
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
open class BaseApplication : Application() {
    object AppContext : ContextWrapper(mApplication)//ContextWrapper对Context上下文进行包装(装饰者模式)

    companion object {
        lateinit var mApplication: BaseApplication
    }

    override fun onCreate() {
        super.onCreate()
        mApplication = this
    }
}

