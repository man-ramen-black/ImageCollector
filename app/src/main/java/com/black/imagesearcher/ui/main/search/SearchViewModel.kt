package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.data.SearchRepository
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val model: SearchRepository
): EventViewModel() {
    companion object {
        const val EVENT_START_DETAIL = "EVENT_SHOW_DETAIL"
    }

    val searchKeyword = MutableLiveData<String>()

    private val favoriteFlow = model.getFavoriteFlow()
    val searchFlow: Flow<PagingData<SearchItem>> = model.getSearchPagingFlow(searchKeyword.asFlow())
        .map { paging ->
            paging.map {
                // PagingContent -> SearchItem.ContentItem으로 변환
                SearchItem.ContentItem(
                    it.content,
                    it.page,
                    // 좋아요 부분만 on/off되도록 flow -> liveData 적용
                    favoriteFlow.map { favorite -> favorite.contains(it.content) }
                        .asLiveData(),
                    { content -> onClickContent(content) },
                    { content -> onClickFavorite(content) },
                ) as SearchItem
            }
        }
        .map {
            // 구분선 추가
            it.insertSeparators { before, after ->
                if (before !is SearchItem.ContentItem) {
                    return@insertSeparators null
                }

                if (after == null) {
                    SearchItem.PageDivider(SearchItem.PageDivider.PAGE_LAST)
                } else if (after is SearchItem.ContentItem && after.page != before.page) {
                    SearchItem.PageDivider(before.page)
                } else {
                    null
                }
            }
        }
        .cachedIn(viewModelScope)

    private fun onClickContent(content: Content) {
        Log.v(content)
        sendEvent(EVENT_START_DETAIL, JsonUtil.to(content))
    }

    private fun onClickFavorite(content: Content) = viewModelScope.launch {
        Log.v(content)
        model.toggleFavorite(content)
    }

    fun onClickDelete() {
        searchKeyword.value = ""
    }
}