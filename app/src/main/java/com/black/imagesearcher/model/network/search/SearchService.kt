package com.black.imagesearcher.model.network.search

import com.black.imagesearcher.model.data.NetworkResult
import com.black.imagesearcher.model.network.KotlinSerialization
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface SearchService {
    companion object {
        private const val REST_API_KEY = "KakaoAK b73c692b4a2836725136a51192c686f2"
    }
        /**
         * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
         * https://dapi.kakao.com/v2/search/image
         */
        @KotlinSerialization
        @Headers("Authorization: $REST_API_KEY")
        @GET("https://dapi.kakao.com/v2/search/image")
        suspend fun searchImage(
            @Query("query") query: String,
            @Query("sort") sort: String,
            @Query("page") page: Int,
            @Query("size") size: Int,
        ): NetworkResult<ImageSearchResponse>

    /**
     * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-video
     * https://dapi.kakao.com/v2/search/vclip
     */
    @KotlinSerialization
    @Headers("Authorization: $REST_API_KEY")
    @GET("https://dapi.kakao.com/v2/search/vclip")
    suspend fun searchVideo(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): NetworkResult<VideoSearchResponse>
}