package com.black.imagesearcher.data.network.search

import com.black.imagesearcher.data.model.NetworkResult
import com.black.imagesearcher.data.network.Network

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
    ): NetworkResult<VideoSearchResponse> {
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