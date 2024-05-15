package com.black.imagesearcher.ui.main.favorite

import androidx.lifecycle.viewModelScope
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.data.SearchRepository
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [FavoriteFragment]
 **/
@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val model: SearchRepository
): EventViewModel() {
    companion object {
        const val EVENT_SHOW_DETAIL = "EVENT_SHOW_DETAIL"
    }

    val favoriteFlow = model.getFavoriteFlow()
        .map { favorite ->
            favorite.map { content ->
                FavoriteItem(
                    content,
                    { onClickContent(it) },
                    { onClickToggleFavorite(it) }
                )
            }
        }

    private fun onClickContent(content: Content) = viewModelScope.launch {
        Log.v(content)
        sendEvent(EVENT_SHOW_DETAIL, content)
    }

    private fun onClickToggleFavorite(content: Content) = viewModelScope.launch {
        Log.v(content)
        model.toggleFavorite(content)
    }
}