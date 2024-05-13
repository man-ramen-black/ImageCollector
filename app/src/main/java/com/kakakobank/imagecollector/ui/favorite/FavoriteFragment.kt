package com.kakakobank.imagecollector.ui.favorite

import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class FavoriteFragment: BaseFragment<FragmentFavoriteBinding>() {
    private val viewModel: FavoriteViewModel by viewModels()
    private val adapter by lazy { FavoriteAdapter(viewModel) }

    override val layoutResId: Int = R.layout.fragment_favorite

    override fun onBindVariable(binding: FragmentFavoriteBinding) {
        binding.adapter = adapter
        lifecycleScope.launch {
            viewModel.favoriteFlow.collectLatest { adapter.submitList(it.toList()) }
        }
    }
}