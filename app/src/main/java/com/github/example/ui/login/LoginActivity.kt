package com.github.example.ui.login

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.example.R

//@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initFragment()
    }

    private fun initFragment() {
        supportFragmentManager.apply {
            findFragmentByTag("LoginFragment") ?: beginTransaction()
                .add(R.id.flContainer, LoginFragment(), "LoginFragment")
                .commitAllowingStateLoss()
        }
    }

}
