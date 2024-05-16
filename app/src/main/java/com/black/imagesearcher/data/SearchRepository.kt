package com.black.imagesearcher.data

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.black.imagesearcher.data.datastore.SearchDataStore
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.data.model.Contents
import com.black.imagesearcher.data.model.NetworkResult
import com.black.imagesearcher.data.model.PagingContent
import com.black.imagesearcher.data.model.ServerError
import com.black.imagesearcher.data.model.TypeContents
import com.black.imagesearcher.data.network.search.SearchApi
import com.black.imagesearcher.data.network.search.SortType
import com.black.imagesearcher.data.paging.SearchPagingSource
import com.black.imagesearcher.data.preferences.SearchPreferences
import com.black.imagesearcher.ui.main.search.SearchViewModel
import com.black.imagesearcher.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.yield
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject

/**
 * [SearchViewModel]
 */
class SearchRepository @Inject constructor(
    private val dataStore: SearchDataStore,
    private val preferences: SearchPreferences
) {
    companion object {
        private const val PAGE_SIZE = 10
        private val SORT_TYPE = SortType.RECENCY
        private const val SEARCH_DELAY = 500L
        private const val DATE_FORMAT_ISO8601 = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"

        private const val USE_DATA_STORE = false
    }

    private val searchEndCache = SearchEndCache()

    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    fun getSearchPagingFlow(searchKeywordFlow: Flow<String>): Flow<PagingData<PagingContent>> {
        return searchKeywordFlow
            .distinctUntilChanged()
            // 타이핑 즉시 검색을 막기 위해 딜레이 적용
            .debounce(SEARCH_DELAY)
            .flatMapLatest { keyword ->
                Pager(PagingConfig(pageSize = PAGE_SIZE * Content.Type.entries.count())) {
                    SearchPagingSource { searchAll(keyword, it) }
                }.flow
            }
    }

    private suspend fun searchAll(keyword: String, page: Int): Result<Contents> = withContext(Dispatchers.IO)  {
        if (keyword.isEmpty()) {
            return@withContext Result.success(Contents(emptyList(), true))
        }

        val asyncList = Content.Type.entries
            // 검색이 끝나지 않은 타입만 조회
            .filter { !searchEndCache.isEnd(keyword, it) }
            .map { type ->
                async {
                    val result = search(type, keyword, page)
                    Log.d(result)
                    yield()
                    result
                }
            }

        val results = asyncList.awaitAll()
        val errorResult = results.filterIsInstance(NetworkResult.Error::class.java)
            .firstOrNull()
        if (errorResult != null) {
            return@withContext Result.failure(errorResult.exception)
        }

        // 검색이 끝난 타입 저장
        results.filter { it.data!!.isEnd }
            .forEach { searchEndCache.setEnd(keyword, it.data!!.type) }

        val resultContents =
            // 결과에서 contents를 추출해서
            results.mapNotNull { it.data }
            // Contents를 합치고
            .fold(Contents(emptyList(), true)) { total, item ->
                Contents(total.contents + item.contents, total.isEnd && item.isEnd)
            }
            // dateTime 최신순으로 정렬
            .let { it.copy(contents = it.contents.sortedByDescending { content -> content.dateTime }) }

        return@withContext Result.success(resultContents)
    }

    private suspend fun search(type: Content.Type, keyword: String, page: Int): NetworkResult<TypeContents> {
        return when (type) {
            Content.Type.IMAGE -> searchImage(keyword, page)
            Content.Type.VIDEO -> searchVideo(keyword, page)
        }
    }

    private suspend fun searchImage(keyword: String, page: Int): NetworkResult<TypeContents> {
        /** API Call */
        val result = SearchApi.searchImage(
            keyword,
            page,
            PAGE_SIZE,
            SORT_TYPE
        )
        Log.d(result)

        if (result is NetworkResult.Error) {
            return NetworkResult.Error(result.exception, result.response)
        }

        result as NetworkResult.Success

        val body = result.data
        val documents = body.documents
            ?: return NetworkResult.Error(ServerError("[${body.errorType}] ${body.message}"), result.response)

        val isEnd = body.meta?.isEnd ?: true
        val contentsList = documents
            .map {
                Content(
                    Content.Type.IMAGE,
                    it.thumbnailUrl,
                    it.imageUrl,
                    it.displaySiteName,
                    it.collection,
                    it.docUrl,
                    toTimeMillis(it.datetime)
                )
            }
        return NetworkResult.Success(TypeContents(Content.Type.IMAGE, contentsList, isEnd), result.response)
    }

    private suspend fun searchVideo(keyword: String, page: Int): NetworkResult<TypeContents> {
        /** API Call */
        val result = SearchApi.searchVideo(
            keyword,
            page,
            PAGE_SIZE,
            SORT_TYPE
        )
        Log.d(result)

        if (result is NetworkResult.Error) {
            return NetworkResult.Error(result.exception, result.response)
        }

        result as NetworkResult.Success

        val body = result.data
        val documents = body.documents
            ?: return NetworkResult.Error(ServerError("[${body.errorType}] ${body.message}"))

        val isEnd = body.meta?.isEnd ?: true
        val contentsList = documents
            .map {
                Content(
                    Content.Type.VIDEO,
                    it.thumbnail,
                    null,
                    it.title,
                    "",
                    it.url,
                    toTimeMillis(it.datetime)
                )
            }
        return NetworkResult.Success(TypeContents(Content.Type.VIDEO, contentsList, isEnd), result.response)
    }

    fun getFavoriteFlow(): Flow<Set<Content>> {
        return if (USE_DATA_STORE) {
            dataStore.getFavoriteFlow()
        } else {
            preferences.getFavoriteFlow()
        }
    }

    suspend fun toggleFavorite(content: Content) {
        Log.d(content)
        val favoriteSet = if (USE_DATA_STORE) {
            dataStore.getFavorite()
        } else {
            preferences.getFavorite()
        }.toMutableSet()

        if (favoriteSet.contains(content)) {
            favoriteSet.remove(content)
        } else {
            favoriteSet.add(content)
        }

        if (USE_DATA_STORE) {
            dataStore.updateFavorite(favoriteSet)
        } else {
            preferences.updateFavorite(favoriteSet)
        }
    }

    private fun toTimeMillis(iso8601: String): Long {
        return try {
            SimpleDateFormat(DATE_FORMAT_ISO8601, Locale.getDefault())
                .parse(iso8601)
                ?.time ?: 0L
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }

    private class SearchEndCache {
        private val endedTypes = ConcurrentHashMap<String, MutableSet<Content.Type>>()

        fun isEnd(keyword: String, type: Content.Type): Boolean {
            if (!endedTypes.containsKey(keyword) && endedTypes.isNotEmpty()) {
                // 다른 검색어로 isEnd 확인을 시도하면 map clear
                endedTypes.clear()
            }
            return endedTypes[keyword]?.contains(type) ?: false
        }

        fun setEnd(keyword: String, type: Content.Type?) {
            endedTypes.getOrPut(keyword) { mutableSetOf() }
                .add(type ?: return)
        }
    }
}