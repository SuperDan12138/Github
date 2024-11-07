package com.github.example.ui

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import com.github.example.R
import com.github.example.base.view.fragment.BaseFragment
import com.github.example.ui.home.HomeFragment
import com.github.example.ui.login.LoginFragment
import com.github.example.ui.repos.ReposFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_main.*

@Suppress("PLUGIN_WARNING")
@AndroidEntryPoint
class MainFragment : BaseFragment() {

    override val layoutId: Int = R.layout.fragment_main

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPager.adapter = com.github.example.adapter.ViewPagerAdapter(
            childFragmentManager,
            listOf(HomeFragment(), ReposFragment(), LoginFragment())
        )
        viewPager.offscreenPageLimit = 2
        bindData()
    }

    private fun bindData() {
        navigation.setOnNavigationItemSelectedListener { menuItem ->
            onBottomNavigationSelectChanged(menuItem)
            true
        }
    }

    // port-mode only
    private fun onBottomNavigationSelectChanged(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.nav_home -> {
                viewPager.currentItem = 0
            }

            R.id.nav_repos -> {
                viewPager.currentItem = 1
            }

            R.id.nav_profile -> {
                viewPager.currentItem = 2
            }
        }
    }
}
