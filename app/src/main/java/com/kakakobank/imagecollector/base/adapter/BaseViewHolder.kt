package com.kakakobank.imagecollector.base.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView

abstract class BaseViewHolder<out BINDING : ViewDataBinding, DATA>(protected val binding: BINDING)
    : RecyclerView.ViewHolder(binding.root) {
    abstract fun bind(item : DATA)
}