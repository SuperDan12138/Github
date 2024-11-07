package com.github.example.utils

import android.content.Context
import androidx.fragment.app.FragmentActivity
import com.github.example.ui.repos.RepoDetailActivity

fun Context.jumpBrowser(activity: FragmentActivity, url: String) {
    RepoDetailActivity.launch(activity, url)
}