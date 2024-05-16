package com.black.imagesearcher.ui.main.favorite

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.black.imagesearcher.R
import com.black.imagesearcher.base.adapter.BaseListAdapter
import com.black.imagesearcher.base.adapter.BaseViewHolder
import com.black.imagesearcher.databinding.ItemFavoriteBinding

/**
 * [FavoriteFragment]
 **/
class FavoriteAdapter: BaseListAdapter<FavoriteItem>(
    { old, new -> old.content == new.content },
    { old, new -> old.content == new.content }
) {
    class ViewHolder(binding: ItemFavoriteBinding): BaseViewHolder<ItemFavoriteBinding, FavoriteItem>(binding) {
        override fun bind(item: FavoriteItem) {
            binding.item = item
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding, FavoriteItem> {
        return ViewHolder(inflateForViewHolder(parent, R.layout.item_favorite))
    }
}