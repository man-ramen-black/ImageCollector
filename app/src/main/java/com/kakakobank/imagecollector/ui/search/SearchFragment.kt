package com.kakakobank.imagecollector.ui.search

import androidx.fragment.app.viewModels
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentSearchBinding

class SearchFragment: BaseFragment<FragmentSearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()
    private val adapter by lazy { SearchAdapter(viewModel) }

    override val layoutResId: Int = R.layout.fragment_search

    override fun onBindVariable(binding: FragmentSearchBinding) {
        binding.viewModel = viewModel
        binding.adapter = adapter
        viewModel.itemList.observe(viewLifecycleOwner) { adapter.submitList(it) }
    }
}