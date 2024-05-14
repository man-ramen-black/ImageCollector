package com.black.imagesearcher.model

import com.black.imagesearcher.model.data.Content
import com.black.imagesearcher.model.data.NetworkResult
import com.black.imagesearcher.model.data.ServerException
import com.black.imagesearcher.model.datastore.SearchDataStore
import com.black.imagesearcher.model.network.search.SearchApi
import com.black.imagesearcher.model.network.search.SortType
import com.black.imagesearcher.ui.main.search.SearchViewModel
import com.black.imagesearcher.util.Log
import kotlinx.coroutines.flow.Flow
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

/**
 * [SearchViewModel]
 */
class SearchModel @Inject constructor(
    private val dataStore: SearchDataStore
) {
    companion object {
        private const val PAGE_SIZE = 10
        private val SORT_TYPE = SortType.RECENCY
    }

    suspend fun searchImage(keyword: String, page: Int): NetworkResult<Pair<Boolean, List<Content>>> {
        val result = SearchApi.searchImage(
            keyword,
            page,
            PAGE_SIZE,
            SORT_TYPE
        )
        Log.d(result)

        if (!result.isSuccess) {
            return NetworkResult.failure(result)
        }

        val documents = result.response?.documents
            ?: return NetworkResult.failure(result, ServerException("[${result.response?.errorType}] ${result.response?.message}"))

        val isEnd = result.response.meta?.isEnd ?: true
        val contentLists = documents
            .map {
                Content(
                    Content.Type.IMAGE,
                    it.thumbnailUrl,
                    it.displaySiteName,
                    it.collection,
                    it.docUrl,
                    toTimeMillis(it.datetime)
                )
            }
        return NetworkResult.success(result, isEnd to contentLists)
    }

    suspend fun searchVideo(keyword: String, page: Int): NetworkResult<Pair<Boolean, List<Content>>> {
        val result = SearchApi.searchVideo(
            keyword,
            page,
            PAGE_SIZE,
            SORT_TYPE
        )
        Log.d(result)

        if (!result.isSuccess) {
            return NetworkResult.failure(result)
        }

        val documents = result.response?.documents
            ?: return NetworkResult.failure(result, ServerException("[${result.response?.errorType}] ${result.response?.message}"))

        val isEnd = result.response.meta?.isEnd ?: true
        val contentLists = documents
            .map {
                Content(
                    Content.Type.VIDEO,
                    it.thumbnail,
                    it.title,
                    "",
                    it.url,
                    toTimeMillis(it.datetime)
                )
            }
        return NetworkResult.success(result, isEnd to contentLists)
    }

    fun getFavoriteFlow(): Flow<Set<Content>> {
        return dataStore.getFavoriteFlow()
    }

    suspend fun toggleFavorite(content: Content) {
        val favoriteSet = dataStore.getFavorite()
            .toMutableSet()
        if (favoriteSet.contains(content)) {
            favoriteSet.remove(content)
        } else {
            favoriteSet.add(content)
        }
        dataStore.updateFavorite(favoriteSet)
    }

    private fun toTimeMillis(iso8601: String): Long {
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                .parse(iso8601)
                ?.time ?: 0L
        } catch (e: ParseException) {
            e.printStackTrace()
            0L
        }
    }
}