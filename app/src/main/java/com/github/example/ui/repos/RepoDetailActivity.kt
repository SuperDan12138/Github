package com.github.example.ui.repos

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.FragmentActivity
import com.github.example.R
import com.github.example.base.view.activity.BaseActivity
import com.github.example.utils.no
import com.github.example.utils.otherwise
import com.github.example.utils.yes
import com.github.example.web.WebDelegate

class RepoDetailActivity : BaseActivity() {
    private lateinit var mWebDelegate: WebDelegate
    private lateinit var mWebUrl: String
    private lateinit var mToolbar: Toolbar

    override val layoutId: Int
        get() = R.layout.activity_repos_detail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initView()
    }

    private fun initView() {
        mWebUrl = intent.getStringExtra(WEB_URL)!!
        mWebDelegate =
            WebDelegate.create(AgentWebContainer(), this, findViewById(R.id.mRootView), mWebUrl)
        lifecycle.addObserver(mWebDelegate)
        mToolbar = findViewById(R.id.mToolbar)
        setSupportActionBar(mToolbar)
        supportActionBar?.let { actionBar ->
            actionBar.setDisplayHomeAsUpEnabled(true)//添加默认的返回图标
            actionBar.setHomeButtonEnabled(true)//设置返回键可用
        }
        initListener()
    }

    private fun initListener() {
        mToolbar.setNavigationOnClickListener {
            mWebDelegate.back().no {
                finish()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean =
        (mWebDelegate.handleKeyEvent(keyCode, event)).yes {
            true
        }.otherwise {
            super.onKeyDown(keyCode, event)
        }

    companion object {
        const val WEB_URL = "web_url"

        fun launch(activity: FragmentActivity, url: String) = activity.apply {
            startActivity(Intent(this, RepoDetailActivity::class.java).putExtra(WEB_URL, url))
        }
    }
}