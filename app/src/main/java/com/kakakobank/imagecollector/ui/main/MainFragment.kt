package com.kakakobank.imagecollector.ui.main

import android.os.Bundle
import android.view.View
import com.kakakobank.imagecollector.base.component.BaseFragment
import com.kakakobank.imagecollector.ui.favorite.FavoriteFragment
import com.kakakobank.imagecollector.ui.search.SearchFragment
import com.kakakobank.imagecollector.util.Util.setupWithViewPager2
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.FragmentMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment: BaseFragment<FragmentMainBinding>() {

    private val tabs by lazy {
        listOf(
            MainTab(
                getString(R.string.main_tab_search)
            ) { SearchFragment() },
            MainTab(
                getString(R.string.main_tab_favorite)
            ) { FavoriteFragment() }
        )
    }
    private lateinit var adapter: MainTabAdapter

    override val layoutResId: Int = R.layout.fragment_main

    override fun onBindVariable(binding: FragmentMainBinding) {
        adapter = MainTabAdapter(this)
        binding.adapter = adapter
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.executePendingBindings()
        binding.tabLayout.setupWithViewPager2(
            viewPager = binding.viewPager,
            smoothScroll = false,
            tabText = { _, position -> tabs[position].title }
        )

        adapter.submitList(tabs)
    }
}