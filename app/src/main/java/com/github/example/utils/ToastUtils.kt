package com.github.example.utils

import com.github.example.ext.toast
import com.github.example.base.BaseApplication

fun toast(value: String): Unit =
        BaseApplication.mApplication.toast(value)
