package com.github.example.ui.search

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.github.example.R
import com.github.example.base.view.fragment.BaseFragment
import com.github.example.ext.observe
import com.github.example.ui.home.RepoItemAdapter
import com.github.example.ui.repos.RepoDetailActivity
import com.github.example.viewmodel.SearchViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_repos.mRecyclerView
import kotlinx.android.synthetic.main.fragment_repos.toolbar
import kotlinx.android.synthetic.main.fragment_search.*

@AndroidEntryPoint
class SearchFragment : BaseFragment() {

    override val layoutId = R.layout.fragment_search

    private val mViewModel: SearchViewModel by viewModels()

    private val mAdapter = RepoItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.menu_repos_search_filter)
        initViews()
        binds()
    }

    private fun initViews() {
        //显示提交按钮
        searchView.isSubmitButtonEnabled = true
        mRecyclerView.adapter = mAdapter.withLoadStateFooter(SearchLoadStateAdapter(mAdapter))
    }

    private fun binds() {
        // navigation clicked event.
        toolbar.setNavigationOnClickListener {
            activity?.finish()
        }

        // menu item clicked event.
        toolbar.setOnMenuItemClickListener {
            onMenuSelected(it)
            true
        }

        // search button clicked event.
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                mViewModel.search(query)
                return false
            }

            override fun onQueryTextChange(newText: String?) = false
        })

        // when button was clicked, scrolling list to top.
        fabTop.setOnClickListener {
            mRecyclerView.scrollToPosition(0)
        }

        observe(mViewModel.repoListLiveData) {
            mAdapter.submitData(lifecycle, it)
            mRecyclerView.scrollToPosition(0)
        }

        observe(mAdapter.observeItemEvent()) {
            RepoDetailActivity.launch(requireActivity(), it)
        }
    }

    private fun onMenuSelected(menuItem: MenuItem) {
        when (menuItem.itemId) {
            R.id.search_by_star -> mViewModel.setSortKey(SearchViewModel.sortByStars)
            R.id.search_by_updated -> mViewModel.setSortKey(SearchViewModel.sortByUpdate)
            else -> throw IllegalArgumentException("failure menuItem id.")
        }
        mAdapter.refresh()
    }
}
