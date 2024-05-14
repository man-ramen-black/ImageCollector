package com.black.imagesearcher.model.network.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
 */
@Serializable
data class ImageSearchResponse(
    val errorType: String?,
    val message: String?,
    val meta: Meta?,
    val documents: List<ImageDocument>?
)

@Serializable
data class Meta(
    @SerialName("total_count") val totalCount: Int,
    @SerialName("pageable_count") val pageableCount: Int,
    @SerialName("is_end") val isEnd: Boolean,
)

@Serializable
data class ImageDocument(
    val collection: String,
    @SerialName("thumbnail_url") val thumbnailUrl: String,
    @SerialName("image_url") val imageUrl: String,
    val width: Int,
    val height: Int,
    @SerialName("display_sitename") val displaySiteName: String,
    @SerialName("doc_url") val docUrl: String,
    val datetime: String
)

@Serializable
data class VideoSearchResponse(
    val errorType: String?,
    val message: String?,
    val meta: Meta?,
    val documents: List<VideoDocument>?
)

@Serializable
data class VideoDocument(
    val title: String,
    @SerialName("play_time") val playTime: Int,
    val thumbnail: String,
    val url: String,
    val datetime: String,
    val author: String
)