package com.black.imagesearcher.ui.detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.black.imagesearcher.base.component.BaseFragment
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.FragmentDetailBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * Created by jinhyuk.lee on 2024/05/13
 **/
@AndroidEntryPoint
class DetailFragment: BaseFragment<FragmentDetailBinding>() {
    private val viewModel: DetailViewModel by viewModels()

    override val layoutResId: Int = R.layout.fragment_detail

    override fun onBindVariable(binding: FragmentDetailBinding) {
        binding.navController = findNavController()
        binding.viewModel = viewModel
    }
}