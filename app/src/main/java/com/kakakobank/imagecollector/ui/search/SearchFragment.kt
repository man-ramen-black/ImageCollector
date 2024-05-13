package com.kakakobank.imagecollector.ui.search

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakakobank.imagecollector.ui.main.MainFragmentDirections
import com.kakakobank.imagecollector.util.FragmentExtension.navigate
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentSearchBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchAdapter

    override val layoutResId: Int = R.layout.fragment_search

    override fun onBindVariable(binding: FragmentSearchBinding) {
        adapter = SearchAdapter(viewModel)
        binding.viewModel = viewModel
        binding.adapter = adapter
        viewModel.observeEvent(viewLifecycleOwner, this)

        lifecycleScope.launch {
            // collect와 collectLatest의 차이
            // collect는 모든 데이터를 순차적으로 처리
            // collectLatest 처리 중 새로운 데이터가 들어오면 기존 데이터를 패스하고 새로운 데이터를 처리
            // https://kotlinworld.com/252
            viewModel.searchFlow.collectLatest { adapter.submitData(it) }
        }
    }

    override fun onReceivedEvent(action: String, data: Any?) {
        super.onReceivedEvent(action, data)
        when (action) {
            SearchViewModel.EVENT_START_DETAIL -> {
                navigate(MainFragmentDirections.actionMainToDetail(data.toString()))
            }
        }
    }
}