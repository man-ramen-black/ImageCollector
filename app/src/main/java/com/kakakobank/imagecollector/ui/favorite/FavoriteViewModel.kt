package com.kakakobank.imagecollector.ui.favorite

import androidx.lifecycle.viewModelScope
import com.kakakobank.imagecollector.base.viewmodel.EventViewModel
import com.kakakobank.imagecollector.model.SearchModel
import com.kakakobank.imagecollector.model.data.Contents
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
    val favoriteFlow = model.getFavoriteFlow()

    fun onClickContents(contents: Contents) = viewModelScope.launch {
        Log.e(contents)
    }

    fun onClickToggleFavorite(contents: Contents) = viewModelScope.launch {
        model.toggleFavorite(contents)
    }
}