package com.github.example.ui.login

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import com.bumptech.glide.request.RequestOptions
import com.github.example.R
import com.github.example.base.view.fragment.BaseFragment
import com.github.example.entity.Error
import com.github.example.ext.observe
import com.github.example.image.GlideApp
import com.github.example.repository.AutoLoginEvent
import com.github.example.utils.toast
import com.github.example.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_login.clProfile
import kotlinx.android.synthetic.main.fragment_login.loginForm
import kotlinx.android.synthetic.main.fragment_login.mBtnSignIn
import kotlinx.android.synthetic.main.fragment_login.mProgressBar
import kotlinx.android.synthetic.main.fragment_login.tvLogout
import kotlinx.android.synthetic.main.fragment_login.tvName
import kotlinx.android.synthetic.main.fragment_login.tvPassword
import kotlinx.android.synthetic.main.fragment_login.tvUsername
import retrofit2.HttpException

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_login

    private val mViewModel: LoginViewModel by viewModels()
    private lateinit var mIcActor: ImageView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binds()
    }

    private fun binds() {
        mBtnSignIn.setOnClickListener {
            mViewModel.login(tvUsername.text.toString(), tvPassword.text.toString())
        }
        tvLogout.setOnClickListener {
            mViewModel.logout()
        }

        mIcActor = activity?.findViewById(R.id.icActor)!!
        observe(mViewModel.stateLiveData, this::onNewState)
        observe(mViewModel.autoLoginLiveData, this::onAutoLogin)
    }

    private fun onAutoLogin(autoLoginEvent: AutoLoginEvent) {
        if (autoLoginEvent.autoLogin) {
            tvUsername.setText(autoLoginEvent.username, TextView.BufferType.EDITABLE)
            tvPassword.setText(autoLoginEvent.password, TextView.BufferType.EDITABLE)

            mViewModel.login(autoLoginEvent.username, autoLoginEvent.password)
        }
    }

    private fun onNewState(state: LoginViewState) {
        if (state.throwable != null) {
            when (state.throwable) {
                is Error.UnknownError -> "username or password can't be null."
                is HttpException ->
                    when (state.throwable.code()) {
                        401 -> "username or password failure."
                        else -> "network failure"
                    }

                else -> "网络异常，请检查你的网络环境（GitHubAPI访问需要梯子）"
            }.also { str ->
                toast(str)
            }
        }

        if (!state.isLoginLayout && state.loginInfo != null) {
            loginForm.visibility = View.GONE
            clProfile.visibility = View.VISIBLE

            GlideApp.with(requireContext())
                .load(state.loginInfo.avatarUrl)
                .apply(RequestOptions().circleCrop())
                .into(mIcActor)
            tvName.text = state.loginInfo.name
        } else {
            loginForm.visibility = View.VISIBLE
            clProfile.visibility = View.GONE
            mProgressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE
        }
    }
}
