package com.github.example.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.example.base.viewmodel.BaseViewModel
import com.github.example.entity.Repo
import com.github.example.repository.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: SearchRepository
) : BaseViewModel() {

    private val mSearchKeyLiveData = MutableLiveData<String>(SEARCH_KEY_DEFAULT)
    private val sortStateLiveData: MutableLiveData<String> = MutableLiveData(sortByUpdate)
    val repoListLiveData: LiveData<PagingData<Repo>> =
        mSearchKeyLiveData.asFlow().flatMapLatest { repository.fetchPager(it).flow.cachedIn(viewModelScope) }.asLiveData()

    init {
        repository.sortKeyProvider = ::fetchSortKey
    }

    fun search(keyWord: String?) {
        if (!keyWord.isNullOrEmpty()) {
            mSearchKeyLiveData.postValue(keyWord)
        }
    }

    @MainThread
    fun setSortKey(sort: String): Boolean {
        return if (sort != fetchSortKey()) {
            sortStateLiveData.postValue(sort)
            true
        } else {
            false
        }
    }

    @MainThread
    fun fetchSortKey(): String {
        return sortStateLiveData.value!!
    }

    companion object {
        const val SEARCH_KEY_DEFAULT = "kotlin"
        const val sortByUpdate: String = "updated"      // default
        const val sortByStars: String = "stars"
    }
}
