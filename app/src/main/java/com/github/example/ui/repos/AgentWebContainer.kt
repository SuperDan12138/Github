package com.github.example.ui.repos

import android.app.Activity
import android.view.KeyEvent
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.github.example.R
import com.github.example.web.WebContainer
import com.just.agentweb.AgentWeb

/**
 * 基于AgentWeb实现
 */
class AgentWebContainer : WebContainer {

    private lateinit var mAgentWeb: AgentWeb

    override fun onCreate(activity: Activity, viewGroup: ViewGroup, webUrl: String) {
        mAgentWeb = AgentWeb.with(activity)
            .setAgentWebParent(viewGroup, LinearLayout.LayoutParams(-1, -1))
            .useDefaultIndicator(ContextCompat.getColor(activity, R.color.white))
            .createAgentWeb()
            .ready()
            .go(webUrl)
    }

    override fun onPause() {
        mAgentWeb.webLifeCycle.onPause()
    }

    override fun onResume() {
        mAgentWeb.webLifeCycle.onResume()
    }

    override fun onDestroy() {
        mAgentWeb.webLifeCycle.onDestroy()
    }

    override fun back() = mAgentWeb.back()

    override fun handleKeyEvent(keyCode: Int, event: KeyEvent) =
        mAgentWeb.handleKeyEvent(keyCode, event)
}