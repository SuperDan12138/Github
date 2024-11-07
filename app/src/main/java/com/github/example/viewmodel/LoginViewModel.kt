package com.github.example.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.github.example.base.viewmodel.BaseViewModel
import com.github.example.entity.Error
import com.github.example.entity.Results
import com.github.example.ext.postNext
import com.github.example.repository.AutoLoginEvent
import com.github.example.repository.LoginRepository
import com.github.example.ui.login.LoginViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressWarnings("checkResult")
class LoginViewModel @Inject constructor(
    private val repo: LoginRepository
) : BaseViewModel() {

    private val _stateLiveData: MutableLiveData<LoginViewState> =
        MutableLiveData(LoginViewState.initial())
    private val autoLoginInfoFlow: Flow<AutoLoginEvent> = repo.fetchAutoLogin().take(1)

    val autoLoginLiveData = autoLoginInfoFlow.asLiveData()
    val stateLiveData: LiveData<LoginViewState> = _stateLiveData

    fun login(username: String?, password: String?) {
        when (username.isNullOrEmpty() || password.isNullOrEmpty()) {
            true -> _stateLiveData.postNext { state ->
                state.copy(
                    isLoading = false,
                    throwable = Error.UnknownError,
                    loginInfo = null,
                    isLoginLayout = true
                )
            }

            false -> viewModelScope.launch {
                _stateLiveData.postNext {
                    it.copy(
                        isLoading = true,
                        throwable = null,
                        loginInfo = null,
                        isLoginLayout = true
                    )
                }
                when (val result = repo.login(username, password)) {
                    is Results.Failure -> _stateLiveData.postNext {
                        it.copy(
                            isLoading = false,
                            throwable = result.error,
                            loginInfo = null,
                            isLoginLayout = true
                        )
                    }

                    is Results.Success -> _stateLiveData.postNext {
                        it.copy(
                            isLoading = false,
                            throwable = null,
                            loginInfo = result.data,
                            isLoginLayout = false
                        )
                    }
                }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _stateLiveData.postNext {
                it.copy(isLoading = false, throwable = null, loginInfo = null, isLoginLayout = true)
            }
            repo.clearLocalLoginInfo()
        }
    }
}
