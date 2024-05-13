package com.kakakobank.imagecollector.ui.favorite

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakakobank.imagecollector.ui.main.MainFragmentDirections
import com.kakakobank.imagecollector.util.FragmentExtension.navigate
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment: BaseFragment<FragmentFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels()
    private lateinit var adapter: FavoriteAdapter

    override val layoutResId: Int = R.layout.fragment_favorite

    override fun onBindVariable(binding: FragmentFavoriteBinding) {
        adapter = FavoriteAdapter(viewModel)
        binding.adapter = adapter
        viewModel.observeEvent(viewLifecycleOwner, this)

        lifecycleScope.launch {
            viewModel.favoriteFlow.collectLatest { adapter.submitList(it.toList()) }
        }
    }

    override fun onReceivedEvent(action: String, data: Any?) {
        when (action) {
            FavoriteViewModel.EVENT_SHOW_DETAIL -> {
                navigate(MainFragmentDirections.actionMainToDetail(data.toString()))
            }
        }
    }
}