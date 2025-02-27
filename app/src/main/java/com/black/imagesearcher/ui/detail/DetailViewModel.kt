package com.black.imagesearcher.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.data.SearchRepository
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * [DetailFragment]
 * SavedStateHandle : https://proandroiddev.com/passing-safe-args-to-your-viewmodel-with-hilt-366762ff3f57
 **/
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val model: SearchRepository,
    saveStateHandle: SavedStateHandle
): EventViewModel() {

    val content = saveStateHandle.get<Content>("content")

    val isFavorite = model.getFavoriteFlow()
        .map { it.contains(content) }
        .asLiveData()

    fun onClickFavorite() {
        Log.v()
        launchSingle {
            model.toggleFavorite(content ?: return@launchSingle)
        }
    }
}