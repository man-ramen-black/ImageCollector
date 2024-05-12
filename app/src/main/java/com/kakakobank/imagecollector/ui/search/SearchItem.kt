package com.kakakobank.imagecollector.ui.search

import com.kakakobank.imagecollector.model.data.Contents

sealed class SearchItem {
    data class ContentsItem(val contents: Contents) : SearchItem()
    data class PageDivider(val page: Int) : SearchItem()
}
