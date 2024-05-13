package com.kakakobank.imagecollector.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.KBJson
import com.kakakobank.imagecollector.util.Log
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
        ?.let { KBJson.from(it, Contents::class.java, true) }

    val isFavorite = model.getFavoriteFlow()
        .map { it.contains(content) }
        .asLiveData()

    fun onClickFavorite() = viewModelScope.launch {
        Log.v()
        model.toggleFavorite(content ?: return@launch)
    }
}