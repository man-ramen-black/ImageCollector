package com.kakakobank.imagecollector.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.yield
import javax.inject.Inject

/**
 * [SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val model: SearchModel
): EventViewModel() {
    companion object {
        private const val SEARCH_DELAY = 500L
    }

    val searchKeyword = MutableLiveData<String>()

    val searchFlow = searchKeyword.asFlow()
        .flatMapLatest {
            delay(500L)
            yield()
            Pager(PagingConfig(pageSize = 20)) {
                SearchPagingSource(model, it)
            }.flow
        }.cachedIn(viewModelScope)

    fun onClickFavorite(item: SearchItem.ContentsItem) {
        Log.e(item)
    }

    fun onClickContents(item: SearchItem.ContentsItem) {
        Log.e(item)
    }

    fun onClickDelete() {
        searchKeyword.value = ""
    }
}