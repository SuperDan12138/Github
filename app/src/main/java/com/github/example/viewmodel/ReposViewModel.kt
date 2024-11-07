package com.github.example.viewmodel

import androidx.annotation.MainThread
import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.github.example.base.viewmodel.BaseViewModel
import com.github.example.entity.Repo
import com.github.example.repository.ReposRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
@SuppressWarnings("checkResult")
class ReposViewModel @Inject constructor(
        private val repository: ReposRepository
) : BaseViewModel() {

    private val _viewStateLiveData: MutableLiveData<String> = MutableLiveData(sortByUpdate)

    val pagedListLiveData: LiveData<PagingData<Repo>>
        get() = repository.fetchRepoPager().flow.cachedIn(viewModelScope).asLiveData()

    init {
        repository.sortKeyProvider = ::fetchSortKey
    }

    @MainThread
    fun setSortKey(sort: String): Boolean {
        return if (sort != fetchSortKey()) {
            _viewStateLiveData.postValue(sort)
            true
        } else {
            false
        }
    }

    @MainThread
    fun fetchSortKey(): String {
        return _viewStateLiveData.value!!
    }

    fun createIssue(title: String, body: String, owner: String, repo: String) {
        viewModelScope.launch {
            repository.createIssue(title, body,owner, repo)
        }
    }

    companion object {

        const val sortByCreated: String = "created"
        const val sortByUpdate: String = "updated"      // default
        const val sortByLetter: String = "full_name"
    }
}
