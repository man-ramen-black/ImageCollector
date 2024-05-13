package com.kakakobank.imagecollector.ui.search

import androidx.lifecycle.LiveData
import com.kakakobank.imagecollector.model.data.Contents

sealed class SearchItem {
    data class ContentsItem(val contents: Contents, val isFavorite: LiveData<Boolean>) : SearchItem()
    data class PageDivider(val page: Int) : SearchItem()
}
