package com.github.example.http.intercept

import com.github.example.BuildConfig
import com.github.example.manager.UserManager
import com.github.example.utils.otherwise
import com.github.example.utils.yes
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return request.url.pathSegments.contains("download").yes {//下载Apk，不需要添加请求头
            chain.proceed(request)
        }.otherwise {
            chain.proceed(request.newBuilder().apply {
                when {
                    UserManager.INSTANCE != null -> {//权限接口
                        val auth = "Token ${BuildConfig.USER_ACCESS_TOKEN}"
                        addHeader("Authorization", auth)
                    }
                }

            }.build())
        }
    }
}