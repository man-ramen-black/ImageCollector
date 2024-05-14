package com.black.imagesearcher.base.adapter

import android.annotation.SuppressLint
import androidx.recyclerview.widget.DiffUtil

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
        return (oldItem == newItem)
    }
}