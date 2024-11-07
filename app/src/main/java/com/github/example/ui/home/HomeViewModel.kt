package com.github.example.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.example.base.viewmodel.BaseViewModel
import com.github.example.entity.Repo
import com.github.example.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
@SuppressWarnings("checkResult")
class HomeViewModel @Inject constructor(
    repository: HomeRepository
) : BaseViewModel() {

    val eventListLiveData: LiveData<PagingData<Repo>> =
        repository.fetchPager().flow.cachedIn(viewModelScope).asLiveData()

}
