package com.black.imagesearcher.ui.main

import android.os.Bundle
import android.view.View
import com.black.imagesearcher.base.component.BaseFragment
import com.black.imagesearcher.ui.main.favorite.FavoriteFragment
import com.black.imagesearcher.ui.main.search.SearchFragment
import com.black.imagesearcher.util.Util.setupWithViewPager2
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.FragmentMainBinding
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
        adapter = MainTabAdapter(this, tabs)
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
    }
}