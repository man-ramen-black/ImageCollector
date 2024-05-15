package com.black.imagesearcher.ui.main.search

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.black.imagesearcher.model.data.Contents
import com.black.imagesearcher.model.data.PagingContent
import java.util.concurrent.atomic.AtomicInteger

/**
 *  https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data?hl=ko#key-value
 */
class SearchPagingSource(private val search: suspend (page:Int) -> Result<Contents>): PagingSource<Int, PagingContent>() {

    private var page = AtomicInteger(1)

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PagingContent> {
        val loadPage = params.key
            ?.also { page.set(it) }
            ?: page.get()

        val result = search(loadPage)
        val data = result.getOrNull()
            ?: return LoadResult.Error(result.exceptionOrNull() ?: UnknownError())

        val isEnd = data.isEnd
        val itemList = result.getOrNull()!!.contents
            .map { PagingContent(loadPage, it) }

        return LoadResult.Page(
            itemList,
            null,
            if (!isEnd) page.incrementAndGet() else null
        )
    }

    override fun getRefreshKey(state: PagingState<Int, PagingContent>): Int? {
        val anchorPosition = state.anchorPosition
            ?: return null
        val anchorPage = state.closestPageToPosition(anchorPosition)
        return anchorPage?.prevKey?.plus(1)
            ?: anchorPage?.nextKey?.minus(1)
    }
}