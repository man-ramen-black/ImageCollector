package com.kakakobank.imagecollector.ui.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.util.concurrent.atomic.AtomicInteger

/**
 *  https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data?hl=ko#key-value
 */
class SearchPagingSource(private val searchModel: SearchModel, private val query: String): PagingSource<Int, SearchItem>() {
    private var page = AtomicInteger(1)
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SearchItem> = withContext(Dispatchers.Main) {
        val loadPage = params.key
            ?.also { page.set(it) }
            ?: page.get()

        // TODO isEnd 후 미호출 처리 추가
        val imageSearch = async {
            val result = searchModel.searchImage(query, loadPage)
            yield()
            result
        }

        // TODO isEnd 후 미호출 처리 추가
        val videoSearch = async {
            val result = searchModel.searchVideo(query, loadPage)
            yield()
            result
        }

        val results = awaitAll(imageSearch, videoSearch)
        // 실패 결과가 있다면 중단
        val errorResult = results.firstOrNull { !it.isSuccess }
        if (errorResult != null) {
            return@withContext LoadResult.Error(errorResult.exception!!)
        }

        val isEnd = results.all { it.response!!.first }

        // 리스트 join, 정렬
        val itemList = results.fold(listOf<Contents>()) { total, item -> total + item.response!!.second }
            .sortedByDescending { it.dateTime }
            .map { SearchItem.ContentsItem(it) }

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