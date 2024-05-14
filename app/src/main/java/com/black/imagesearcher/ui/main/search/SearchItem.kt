package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.LiveData
import com.black.imagesearcher.model.data.Content

sealed class SearchItem {
    data class ContentItem(val content: Content, val isFavorite: LiveData<Boolean>) : SearchItem()
    data class PageDivider(val page: Int) : SearchItem()
}
