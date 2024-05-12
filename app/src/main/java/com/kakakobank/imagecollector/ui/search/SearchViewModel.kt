package com.kakakobank.imagecollector.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import javax.inject.Inject

/**
 * [SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val model: SearchModel
): EventViewModel() {
    companion object {
        private const val SEARCH_DELAY = 500L
    }

    val itemList = MutableLiveData<List<Contents>>()
    val searchKeyword = MutableLiveData<String>()

    private var searchJob: Job? = null

    fun onTextChanged(text: CharSequence) {
        if (searchJob?.isCompleted == false) {
            // 실행 중인 job 중단
            searchJob?.cancel()
        }

        searchJob = viewModelScope.launch {
            // 타이핑 후 즉시 검색되는 것을 방지하기 위해 딜레이 적용
            delay(SEARCH_DELAY)
            yield()

            val imageSearch = async {
                val result = model.searchImage(text.toString())
                yield()
                result
            }

            val videoSearch = async {
                val result = model.searchVideo(text.toString())
                yield()
                result
            }

            val results = awaitAll(imageSearch, videoSearch)
            // 실패 결과가 있다면 중단
            if (results.firstOrNull { !it.isSuccess } != null) {
                return@launch
            }

            // 리스트 join, 정렬 후 뷰에 설정
            val contentsList = results.fold(listOf<Contents>()) { total, item -> total + item.response!! }
                .sortedByDescending { it.dateTime }
            itemList.value = contentsList
        }
    }

    fun onClickFavorite(contents: Contents) {
        Log.e(contents)
    }

    fun onClickContents(contents: Contents) {
        Log.e(contents)
    }

    fun onClickDelete() {
        searchKeyword.value = ""
    }
}