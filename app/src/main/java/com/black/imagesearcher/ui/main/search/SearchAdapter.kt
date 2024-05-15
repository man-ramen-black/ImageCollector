package com.black.imagesearcher.ui.main.search

import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import com.black.imagesearcher.base.adapter.BasePagingAdapter
import com.black.imagesearcher.base.adapter.BaseViewHolder
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.ItemSearchBinding
import com.black.imagesearcher.databinding.ItemSearchDividerBinding

class SearchAdapter: BasePagingAdapter<SearchItem>() {
    companion object {
        private const val VIEW_TYPE_SEARCH_ITEM = 0
        private const val VIEW_TYPE_DIVIDER = 1
    }

    class SearchViewHolder(binding: ItemSearchBinding): BaseViewHolder<ItemSearchBinding, SearchItem>(binding) {
        override fun bind(item: SearchItem) {
            binding.item = item as SearchItem.ContentItem
        }
    }

    class DividerViewHolder(
        binding: ItemSearchDividerBinding
    ): BaseViewHolder<ItemSearchDividerBinding, SearchItem>(binding) {
        override fun bind(item: SearchItem) {
            binding.item = item as SearchItem.PageDivider
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is SearchItem.ContentItem -> VIEW_TYPE_SEARCH_ITEM
            else -> VIEW_TYPE_DIVIDER
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewDataBinding, SearchItem> {
        return when (viewType) {
            VIEW_TYPE_SEARCH_ITEM -> SearchViewHolder(inflateForViewHolder(parent, R.layout.item_search))
            else -> DividerViewHolder(inflateForViewHolder(parent, R.layout.item_search_divider))
        }
    }
}