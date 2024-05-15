package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.asLiveData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.black.imagesearcher.model.SearchModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicInteger

/**
 *  https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data?hl=ko#key-value
 */
class SearchPagingSource(private val searchModel: SearchModel, private val query: String): PagingSource<Int, SearchItem>() {
    private var page = AtomicInteger(1)
    private val favoriteFlow = searchModel.getFavoriteFlow()

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchItem> = withContext(Dispatchers.Main) {
        val loadPage = params.key
            ?.also { page.set(it) }
            ?: page.get()

        val result = searchModel.searchAll(query, loadPage)
        val data = result.getOrNull()
            ?: return@withContext LoadResult.Error(result.exceptionOrNull() ?: UnknownError())


        val isEnd = data.isEnd
        val itemList = result.getOrNull()!!.contents
            .map { content ->
                SearchItem.ContentItem(
                    content,
                    favoriteFlow.map { it.contains(content) }
                        .asLiveData()
                )
            }

        val listWithDivider = if (loadPage == 1 && itemList.isEmpty() && isEnd) {
            // 첫 페이지에서 검색 결과가 없는 경우 divider를 추가하지 않음
            itemList

        } else {
            val dividerPage = if (isEnd) 0 else loadPage
            itemList + listOf(SearchItem.PageDivider(dividerPage))
        }

        return@withContext LoadResult.Page(
            listWithDivider,
            null,
            if (!isEnd) page.incrementAndGet() else null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, SearchItem>): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1)
            ?: anchorPage?.nextKey?.minus(1)
    }
}

class LoadCompleteException(): Throwable()