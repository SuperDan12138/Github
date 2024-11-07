package com.github.example.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.asLiveData
import androidx.paging.LoadState
import com.github.example.R
import com.github.example.base.view.fragment.BaseFragment
import com.github.example.ext.observe
import com.github.example.ui.repos.RepoDetailActivity
import com.github.example.ui.search.SearchActivity
import com.github.example.utils.removeAllAnimation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_home.*

@AndroidEntryPoint
class HomeFragment : BaseFragment() {

    private val mViewModel: HomeViewModel by viewModels()

    override val layoutId: Int = R.layout.fragment_home

    private val mAdapter: RepoItemAdapter = RepoItemAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.inflateMenu(R.menu.menu_home_search)
        binds()

        mRecyclerView.adapter = mAdapter
        mRecyclerView.removeAllAnimation()
    }

    private fun binds() {
        // when button was clicked, scrolling list to top.
        fabTop.setOnClickListener {
            mRecyclerView.scrollToPosition(0)
        }

        // swipe refresh event.
        mSwipeRefreshLayout.setOnRefreshListener(mAdapter::refresh)

        // search menu item clicked event.
        toolbar.setOnMenuItemClickListener {
            SearchActivity.launch(requireActivity())
            true
        }

        // list item clicked event.
        observe(mAdapter.observeItemEvent()) {
            RepoDetailActivity.launch(requireActivity(), it)
        }

        observe(mAdapter.loadStateFlow.asLiveData()) { loadStates ->
            mSwipeRefreshLayout.isRefreshing = loadStates.refresh is LoadState.Loading
        }

        observe(mViewModel.eventListLiveData) {
            mAdapter.submitData(lifecycle, it)
            mRecyclerView.scrollToPosition(0)
        }
    }
}
