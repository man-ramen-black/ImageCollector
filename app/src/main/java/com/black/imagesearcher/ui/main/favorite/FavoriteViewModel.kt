package com.black.imagesearcher.ui.main.favorite

import androidx.lifecycle.viewModelScope
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.model.SearchModel
import com.black.imagesearcher.model.data.Contents
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [FavoriteFragment]
 **/
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val model: SearchModel
): EventViewModel() {
    companion object {
        const val EVENT_SHOW_DETAIL = "EVENT_SHOW_DETAIL"
    }

    val favoriteFlow = model.getFavoriteFlow()

    fun onClickContents(contents: Contents) = viewModelScope.launch {
        Log.v(contents)
        sendEvent(EVENT_SHOW_DETAIL, JsonUtil.to(contents))
    }

    fun onClickToggleFavorite(contents: Contents) = viewModelScope.launch {
        Log.v(contents)
        model.toggleFavorite(contents)
    }
}