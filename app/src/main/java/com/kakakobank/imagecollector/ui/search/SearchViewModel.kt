package com.kakakobank.imagecollector.ui.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [SearchFragment]
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val model: SearchModel
): EventViewModel() {
    val itemList = MutableLiveData<List<Contents>>()
    val searchKeyword = MutableLiveData<String>()

    fun onTextChanged(text: CharSequence) {
        viewModelScope.launch {
            val result = model.searchImage(text.toString())
            if (!result.isSuccess) {
                return@launch
            }

            itemList.value = result.response!!
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