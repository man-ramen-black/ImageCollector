package com.black.imagesearcher.ui.main.favorite

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.black.imagesearcher.base.adapter.BaseListAdapter
import com.black.imagesearcher.base.adapter.BaseViewHolder
import com.black.imagesearcher.model.data.Content
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.ItemFavoriteBinding

/**
 * [FavoriteFragment]
 **/
class FavoriteAdapter(private val viewModel: FavoriteViewModel): BaseListAdapter<Content>() {
    class ViewHolder(
        binding: ItemFavoriteBinding,
        private val viewModel: FavoriteViewModel
    ): BaseViewHolder<ItemFavoriteBinding, Content>(binding) {
        override fun bind(item: Content) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding, Content> {
        return ViewHolder(inflateForViewHolder(parent, R.layout.item_favorite), viewModel)
    }
}