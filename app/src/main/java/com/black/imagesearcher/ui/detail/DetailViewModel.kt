package com.black.imagesearcher.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.black.imagesearcher.base.viewmodel.EventViewModel
import com.black.imagesearcher.model.SearchModel
import com.black.imagesearcher.model.data.Contents
import com.black.imagesearcher.util.JsonUtil
import com.black.imagesearcher.util.Log
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * [DetailFragment]
 * SavedStateHandle : https://proandroiddev.com/passing-safe-args-to-your-viewmodel-with-hilt-366762ff3f57
 **/
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val model: SearchModel,
    saveStateHandle: SavedStateHandle
): EventViewModel() {

    val content = saveStateHandle.get<String>("contentJson")
        ?.let { JsonUtil.from(it, Contents::class.java, true) }

    val isFavorite = model.getFavoriteFlow()
        .map { it.contains(content) }
        .asLiveData()

    fun onClickFavorite() = viewModelScope.launch {
        Log.v()
        model.toggleFavorite(content ?: return@launch)
    }
}