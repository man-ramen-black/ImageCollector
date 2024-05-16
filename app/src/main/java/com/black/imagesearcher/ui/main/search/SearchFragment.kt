package com.black.imagesearcher.ui.main.search

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import com.black.imagesearcher.R
import com.black.imagesearcher.base.component.BaseFragment
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.databinding.FragmentSearchBinding
import com.black.imagesearcher.ui.detail.DetailFragment
import com.black.imagesearcher.ui.main.MainFragmentDirections
import com.black.imagesearcher.util.FragmentExtension.navigate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchFragment: BaseFragment<FragmentSearchBinding>() {
    private val viewModel: SearchViewModel by viewModels()
    private lateinit var adapter: SearchAdapter

    override val layoutResId: Int = R.layout.fragment_search

    override fun onBindVariable(binding: FragmentSearchBinding) {
        adapter = SearchAdapter()
        binding.adapter = adapter
        binding.viewModel = viewModel
        viewModel.observeEvent(viewLifecycleOwner, this)

        viewLifecycleOwner.lifecycleScope.launch {
            // collect와 collectLatest의 차이
            // collect는 모든 데이터를 순차적으로 처리
            // collectLatest 처리 중 새로운 데이터가 들어오면 기존 데이터를 패스하고 새로운 데이터를 처리
            // https://kotlinworld.com/252
            viewModel.searchFlow.collectLatest { adapter.submitData(it) }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            adapter.loadStateFlow
                // 중복 데이터 패스
                .distinctUntilChangedBy { it.refresh }
                .collectLatest {
                    val refresh = it.refresh
                    if (refresh is LoadState.Error) {
                        showError(refresh.error.message ?: refresh.error::class.java.simpleName)
                    }

                    // 로딩 중
                    viewModel.isProgress.value = refresh is LoadState.Loading

                    // 검색 결과 없음
                    viewModel.isResultEmpty.value = it.append is LoadState.NotLoading
                            // 데이터가 끝났고
                            && it.append.endOfPaginationReached
                            // 리스트가 비었고
                            && adapter.itemCount == 0
                            // 검색어가 있음
                            && !viewModel.searchKeyword.value.isNullOrEmpty()
            }
        }
    }

    override fun onReceivedEvent(action: String, data: Any?) {
        super.onReceivedEvent(action, data)
        when (action) {
            SearchViewModel.EVENT_START_DETAIL -> {
                /** [DetailFragment] */
                navigate(MainFragmentDirections.actionMainToDetail(data as Content))
            }
        }
    }

    private fun showError(message: String) {
        MaterialAlertDialogBuilder(requireActivity())
            .setMessage(getString(R.string.search_error_message, message))
            .setPositiveButton(R.string.retry) { dialog, _ ->
                adapter.retry()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.cancel()
            }
            .show()
    }
}