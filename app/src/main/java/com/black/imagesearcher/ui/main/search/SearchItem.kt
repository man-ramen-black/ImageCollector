package com.black.imagesearcher.ui.main.search

import androidx.lifecycle.LiveData
import com.black.imagesearcher.model.data.Contents

sealed class SearchItem {
    data class ContentsItem(val contents: Contents, val isFavorite: LiveData<Boolean>) : SearchItem()
    data class PageDivider(val page: Int) : SearchItem()
}
