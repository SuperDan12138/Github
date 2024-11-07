package com.github.example.ui

import android.content.Intent
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.github.example.base.view.activity.BaseActivity
import com.github.example.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override val layoutId = R.layout.activity_main

    override fun onSupportNavigateUp(): Boolean =
            Navigation.findNavController(this, R.id.navHostFragment).navigateUp()

    companion object {

        fun launch(activity: FragmentActivity) =
                activity.apply {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
    }
}
