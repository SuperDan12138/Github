package com.github.example.ui.login

import com.github.example.entity.UserInfo

data class LoginViewState(
    val isLoading: Boolean,
    val throwable: Throwable?,
    val loginInfo: UserInfo?,
    val isLoginLayout: Boolean
) {

    companion object {
        fun initial(): LoginViewState {
            return LoginViewState(
                isLoading = false,
                throwable = null,
                loginInfo = null,
                isLoginLayout = true
            )
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LoginViewState

        if (isLoading != other.isLoading) return false
        if (throwable != other.throwable) return false
        if (loginInfo != other.loginInfo) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isLoading.hashCode()
        result = 31 * result + (throwable?.hashCode() ?: 0)
        result = 31 * result + (loginInfo?.hashCode() ?: 0)
        return result
    }
}