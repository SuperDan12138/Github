package com.github.example.repository

import com.github.example.BuildConfig
import com.github.example.entity.Results
import com.github.example.db.UsersDatabase
import com.github.example.entity.UserInfo
import com.github.example.http.service.ServiceManager
import com.github.example.manager.UserManager
import com.github.example.utils.processApiResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LoginRepository @Inject constructor(
    remoteDataSource: LoginRemoteDataSource,
    localDataSource: LoginLocalDataSource
) : com.github.example.base.repository.BaseRepositoryBoth<LoginRemoteDataSource, LoginLocalDataSource>(remoteDataSource, localDataSource) {

    suspend fun login(username: String, password: String): Results<UserInfo> {
        // 保存用户登录信息
        localDataSource.savePrefUser(username, password)
        val userInfo = remoteDataSource.login()

        // 如果登录失败，清除登录信息
        when (userInfo) {
            is Results.Failure -> localDataSource.clearPrefsUser()
            is Results.Success -> UserManager.INSTANCE = requireNotNull(userInfo.data)
        }
        return userInfo
    }

    fun fetchAutoLogin(): Flow<AutoLoginEvent> {
        return localDataSource.fetchAutoLogin()
    }

    //登出 清除登录信息
    suspend fun clearLocalLoginInfo(){
        localDataSource.clearPrefsUser()
        UserManager.INSTANCE = null
    }
}

class LoginRemoteDataSource @Inject constructor(
        private val serviceManager: ServiceManager
) : com.github.example.base.repository.IRemoteDataSource {

    suspend fun login(): Results<UserInfo> {
        val auth = "token ${BuildConfig.USER_ACCESS_TOKEN}"
        return processApiResponse { serviceManager.userService.fetchUserOwner(auth) }
    }
}

class LoginLocalDataSource @Inject constructor(
    private val db: UsersDatabase,
    private val userRepository: UserInfoRepository
) : com.github.example.base.repository.ILocalDataSource {

    suspend fun savePrefUser(username: String, password: String) {
        userRepository.saveUserInfo(username, password)
    }

    suspend fun clearPrefsUser() {
        userRepository.saveUserInfo("", "")
    }

    fun fetchAutoLogin(): Flow<AutoLoginEvent> {
        return userRepository.fetchUserInfoFlow()
                .map { user ->
                    val username = user.username
                    val password = user.password
                    val isAutoLogin = user.autoLogin
                    when (username.isNotEmpty() && password.isNotEmpty() && isAutoLogin) {
                        true -> AutoLoginEvent(true, username, password)
                        false -> AutoLoginEvent(false, "", "")
                    }
                }
    }
}

data class AutoLoginEvent(
        val autoLogin: Boolean,
        val username: String,
        val password: String
)
