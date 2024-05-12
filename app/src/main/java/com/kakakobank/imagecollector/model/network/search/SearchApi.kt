package com.kakakobank.imagecollector.model.network.search

import com.kakakobank.imagecollector.model.data.NetworkResult
import com.kakakobank.imagecollector.model.network.Network

object SearchApi {
    suspend fun searchImage(
        keyword: String,
        page: Int,
        size: Int,
        sortType: SortType
    ): NetworkResult<ImageSearchResponse> {
        return Network.service(SearchService::class.java)
            .searchImage(
                keyword,
                sortType.value,
                page,
                size
            )
    }

    suspend fun searchVideo(
        keyword: String,
        page: Int,
        size: Int,
        sortType: SortType
    ): NetworkResult<ImageSearchResponse> {
        return Network.service(SearchService::class.java)
            .searchVideo(
                keyword,
                sortType.value,
                page,
                size
            )
    }
}

enum class SortType(val value: String) {
    ACCURACY("accuracy"),
    RECENCY("recency")
}