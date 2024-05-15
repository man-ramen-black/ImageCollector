package com.black.imagesearcher.ui.main.favorite

import androidx.lifecycle.viewModelScope
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.data.SearchRepository
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.Log
import com.black.imagesearcher.util.Util.isNotCompleted
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
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
                    { onClickFavorite(it) }
                )
            }
        }

    private fun onClickContent(content: Content) {
        Log.v(content)
        sendEvent(EVENT_SHOW_DETAIL, content)
    }

    private fun onClickFavorite(content: Content) {
        Log.v(content)
        launchSingle {
            model.toggleFavorite(content)
        }
    }
}