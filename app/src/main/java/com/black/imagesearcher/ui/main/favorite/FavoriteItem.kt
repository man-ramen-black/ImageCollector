package com.black.imagesearcher.ui.main.favorite

import com.black.imagesearcher.data.model.Content

data class FavoriteItem(
    val content: Content,
    val onItemClick: (Content) -> Unit,
    val onClickFavorite: (Content) -> Unit,
)