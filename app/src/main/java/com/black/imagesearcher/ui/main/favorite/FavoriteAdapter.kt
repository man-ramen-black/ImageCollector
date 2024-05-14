package com.black.imagesearcher.ui.main.favorite

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.black.imagesearcher.base.adapter.BaseListAdapter
import com.black.imagesearcher.base.adapter.BaseViewHolder
import com.black.imagesearcher.model.data.Contents
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.ItemFavoriteBinding

/**
 * [FavoriteFragment]
 **/
class FavoriteAdapter(private val viewModel: FavoriteViewModel): BaseListAdapter<Contents>() {
    class ViewHolder(
        binding: ItemFavoriteBinding,
        private val viewModel: FavoriteViewModel
    ): BaseViewHolder<ItemFavoriteBinding, Contents>(binding) {
        override fun bind(item: Contents) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding, Contents> {
        return ViewHolder(inflateForViewHolder(parent, R.layout.item_favorite), viewModel)
    }
}