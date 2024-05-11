package com.kakakobank.imagecollector.base.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.viewpager2.adapter.FragmentStateAdapter

abstract class BaseFragmentStateAdapter<T: Any>(
    fragment: FragmentManager,
    lifecycle: Lifecycle,
    itemCallback: DiffUtil.ItemCallback<T> = SimpleItemCallback()
): FragmentStateAdapter(fragment, lifecycle) {

    constructor(
        activity: FragmentActivity,
        itemCallback: DiffUtil.ItemCallback<T> = SimpleItemCallback()
    ): this(
        activity.supportFragmentManager,
        activity.lifecycle,
        itemCallback
    )

    constructor(
        fragment: Fragment,
        itemCallback: DiffUtil.ItemCallback<T> = SimpleItemCallback()
    ): this(
        fragment.childFragmentManager,
        fragment.lifecycle,
        itemCallback
    )

    protected val differ by lazy { AsyncListDiffer(this, itemCallback) }

    protected open fun getItem(position: Int): T {
        return differ.currentList[position]
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * notifyDataSetChanged 동작을 위한 getItemId 구현
     */
    override fun getItemId(position: Int): Long {
        return differ.currentList.getOrNull(position)?.hashCode()?.toLong() ?: 0L
    }

    /**
     * notifyDataSetChanged 동작을 위한 containsItem 구현
     */
    override fun containsItem(itemId: Long): Boolean {
        return differ.currentList.any { it.hashCode().toLong() == itemId }
    }

    fun submitList(list: List<T>) {
        differ.submitList(list)
    }
}