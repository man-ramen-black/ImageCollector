package com.kakakobank.imagecollector.ui.search

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.kakakobank.imagecollector.base.adapter.BaseListAdapter
import com.kakakobank.imagecollector.model.Contents
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.ItemSearchBinding

class SearchAdapter(private val viewModel: SearchViewModel): BaseListAdapter<Contents>() {

    class ViewHolder(
        binding: ItemSearchBinding,
        private val viewModel: SearchViewModel
    ): BaseViewHolder<ItemSearchBinding, Contents>(binding) {
        override fun bind(item: Contents) {
            binding.item = item
            binding.viewModel = viewModel
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding, Contents> {
        return ViewHolder(inflateForViewHolder(parent, R.layout.item_search), viewModel)
    }
}