package com.kakakobank.imagecollector.ui.detail

import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentDetailBinding
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