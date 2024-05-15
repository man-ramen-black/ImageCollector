package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.LiveData
import com.black.imagesearcher.data.model.Content

sealed class SearchItem {
    data class ContentItem(val content: Content, val page: Int, val isFavorite: LiveData<Boolean>) : SearchItem()
    data class PageDivider(val page: Int) : SearchItem() {
        companion object {
            const val PAGE_LAST = -1
        }
    }
}
