package com.black.imagesearcher.ui.main.favorite

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.black.imagesearcher.R
import com.black.imagesearcher.base.component.BaseFragment
import com.black.imagesearcher.data.model.Content
import com.black.imagesearcher.databinding.FragmentFavoriteBinding
import com.black.imagesearcher.ui.detail.DetailFragment
import com.black.imagesearcher.ui.main.MainFragmentDirections
import com.black.imagesearcher.util.FragmentExtension.navigate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment: BaseFragment<FragmentFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: FavoriteAdapter

    override val layoutResId: Int = R.layout.fragment_favorite

    override fun onBindVariable(binding: FragmentFavoriteBinding) {
        adapter = FavoriteAdapter()
        binding.adapter = adapter
        binding.viewModel = viewModel
        viewModel.observeEvent(viewLifecycleOwner, this)

        lifecycleScope.launch {
            viewModel.favoriteFlow.collectLatest { adapter.submitList(it.toList()) }
        }
    }

    override fun onReceivedEvent(action: String, data: Any?) {
        when (action) {
            FavoriteViewModel.EVENT_SHOW_DETAIL -> {
                /** [DetailFragment] */
                navigate(MainFragmentDirections.actionMainToDetail(data as Content))
            }
        }
    }
}