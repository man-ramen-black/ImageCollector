package com.black.imagesearcher.base.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

/*
#RecyclerView #ListAdapter
https://youngest-programming.tistory.com/474
 */
abstract class BaseListAdapter<DATA : Any>(itemCallback: DiffUtil.ItemCallback<DATA> = SimpleItemCallback())
    : ListAdapter<DATA, BaseViewHolder<ViewDataBinding, DATA>>(itemCallback) {

    constructor(
        areItemsTheSame : (old: DATA, new: DATA) -> Boolean
    ): this(SimpleItemCallback<DATA>(areItemsTheSame))

    constructor(
        areItemsTheSame : (old: DATA, new: DATA) -> Boolean,
        areContentsTheSame : (old: DATA, new: DATA) -> Boolean
    ) : this(SimpleItemCallback<DATA>(areItemsTheSame, areContentsTheSame))

    override fun onBindViewHolder(holder: BaseViewHolder<ViewDataBinding, DATA>, position: Int) {
        holder.bind(getItem(position))
    }

    protected fun <BINDING : ViewDataBinding> inflateForViewHolder(parent: ViewGroup, layoutId: Int) : BINDING {
        return DataBindingUtil.inflate<BINDING>(LayoutInflater.from(parent.context), layoutId, parent, false)
            .apply {
                lifecycleOwner = parent.findViewTreeLifecycleOwner()
            }
    }

    /*
    submitList는 동일한 list 객체가 전달되는 경우 업데이트 하지 않기 때문에
    toMutalbeList로 MutableList로 객체를 새로 생성하여 처리
    #submitList
    https://stackoverflow.com/a/58080105
     */
    override fun submitList(list: List<DATA>?) {
        super.submitList(if(currentList == list) list.toList() else list)
    }
}