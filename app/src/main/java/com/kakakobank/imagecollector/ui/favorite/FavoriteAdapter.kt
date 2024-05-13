package com.kakakobank.imagecollector.ui.favorite

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.kakakobank.imagecollector.base.adapter.BaseListAdapter
import com.kakakobank.imagecollector.base.adapter.BaseViewHolder
import com.kakakobank.imagecollector.model.data.Contents
import com.kakakobank.imagecollector.util.Log
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.ItemFavoriteBinding

/**
 * [FavoriteFragment]
 **/
class FavoriteAdapter(private val viewModel: FavoriteViewModel): BaseListAdapter<Contents>() {
    class ViewHolder(
        binding: ItemFavoriteBinding,
        private val viewModel: FavoriteViewModel
    ): BaseViewHolder<ItemFavoriteBinding, Contents>(binding) {
        override fun bind(item: Contents) {
            Log.e(item)
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