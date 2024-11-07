package com.github.example.ui.repos

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.github.example.R
import com.github.example.base.view.fragment.BaseFragment
import com.github.example.ext.observe
import com.github.example.ui.IssueDialog
import com.github.example.utils.removeAllAnimation
import com.github.example.viewmodel.ReposViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_repos.fabTop
import kotlinx.android.synthetic.main.fragment_repos.mRecyclerView
import kotlinx.android.synthetic.main.fragment_repos.mSwipeRefreshLayout
import kotlinx.android.synthetic.main.fragment_repos.toolbar

@AndroidEntryPoint
class ReposFragment : BaseFragment() {

    private val mViewModel: ReposViewModel by viewModels()

    override val layoutId: Int = R.layout.fragment_repos

    private val mAdapter = ReposPagedAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.menu_repos_filter_type)

        mRecyclerView.adapter = mAdapter
        mRecyclerView.removeAllAnimation()
        binds()
    }

    private fun binds() {
        // swipe refresh event.
        mSwipeRefreshLayout.setOnRefreshListener {
            mAdapter.refresh()
        }

        // when button was clicked, scrolling list to top.
        fabTop.setOnClickListener {
            mRecyclerView.scrollToPosition(0)
        }

        // menu item clicked event.
        toolbar.setOnMenuItemClickListener {
            onMenuSelected(it)
            true
        }

        // list item clicked event.
        observe(mAdapter.getItemClickEvent()) {
            RepoDetailActivity.launch(requireActivity(), it)
        }

        observe(mAdapter.getItemLongClickEvent()) {
            val owner = it.owner.login
            val repo = it.name

            val dialog = IssueDialog()
            dialog.setOnInputListener(object : IssueDialog.OnInputListener {
                override fun onInput(title: String, body: String) {
                    mViewModel.createIssue(title, body, owner, repo)
                }
            })
            dialog.show(childFragmentManager, "inputDialog")
        }

        observe(mAdapter.loadStateFlow.asLiveData()) { loadStates ->
            mSwipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
        }

        observe(mViewModel.pagedListLiveData) {
            mAdapter.submitData(lifecycle, it)
            mRecyclerView.scrollToPosition(0)
        }
    }

    private fun onMenuSelected(menuItem: MenuItem) {
        val isKeyUpdated = mViewModel.setSortKey(
            when (menuItem.itemId) {
                R.id.menu_repos_letter -> ReposViewModel.sortByLetter
                R.id.menu_repos_update -> ReposViewModel.sortByUpdate
                R.id.menu_repos_created -> ReposViewModel.sortByCreated
                else -> throw IllegalArgumentException("failure menuItem id.")
            }
        )
        if (isKeyUpdated)
            mAdapter.refresh()
    }
}
