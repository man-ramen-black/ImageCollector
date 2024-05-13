package com.kakakobank.imagecollector.ui.favorite

import androidx.lifecycle.viewModelScope
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.KBJson
import com.kakakobank.imagecollector.util.Log
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
        sendEvent(EVENT_SHOW_DETAIL, KBJson.to(contents))
    }

    fun onClickToggleFavorite(contents: Contents) = viewModelScope.launch {
        Log.v(contents)
        model.toggleFavorite(contents)
    }
}