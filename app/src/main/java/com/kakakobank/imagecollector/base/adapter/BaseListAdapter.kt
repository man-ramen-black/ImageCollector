package com.kakakobank.imagecollector.base.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/*
#RecyclerView #ListAdapter
https://youngest-programming.tistory.com/474
 */
abstract class BaseListAdapter<DATA : Any>(itemCallback: DiffUtil.ItemCallback<DATA> = SimpleItemCallback())
    : ListAdapter<DATA, BaseListAdapter.BaseViewHolder<ViewDataBinding, DATA>>(itemCallback) {

    constructor(areItemsTheSame : (oldItem: DATA, newItem: DATA) -> Boolean)
            : this(SimpleItemCallback<DATA>(areItemsTheSame))

    init {
        // 리스트 아이템이 유니크한 getItemId 를 가지고 있고, 동일한 getItemId 일 경우, 갱신하지 않도록 설정
        setHasStableIds(true)
    }

    final override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(hasStableIds)
    }

    override fun getItemId(position: Int): Long {
        // notifyDataSetChanged 시 깜빡임을 방지하기 위해 유니크 값 반환
        return getItem(position).hashCode().toLong()
    }

    abstract class BaseViewHolder<out BINDING : ViewDataBinding, DATA>(protected val binding: BINDING)
        : RecyclerView.ViewHolder(binding.root) {
        abstract fun bind(item : DATA)
    }

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

/*
#DiffUtil
DiffUtil? : https://zion830.tistory.com/86
샘플 코드 : https://www.charlezz.com/?p=1363
왜 Items, Contents가 따로 있는지 : https://stackoverflow.com/questions/72178004/why-use-areitemsthesame-with-arecontentsthesame-at-diffutil-recyclerview
 */
open class SimpleItemCallback<T : Any>(private val areItemsTheSame : (oldItem: T, newItem: T) -> Boolean = { old, new -> old == new})
    : DiffUtil.ItemCallback<T>()
{
    /**
     * 동일한 항목인지 비교 : 리스트의 구조가 바뀌어야 하는지(추가/제거/위치 변경)
     * areItemsTheSame이 true여야만 areContentsTheSame가 호출됨
     * 구조가 변경되는 Adapter에서 override 필수
     */
    override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
        return areItemsTheSame.invoke(oldItem, newItem)
    }

    /**
     * 값이 같은지 비교 : 특정 항목이 갱신되어야 하는지
     * areItemsTheSame이 true인 경우에 호출됨
     */
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
        return oldItem == newItem
    }
}