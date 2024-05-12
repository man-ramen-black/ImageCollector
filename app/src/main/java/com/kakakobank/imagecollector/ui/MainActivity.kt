package com.kakakobank.imagecollector.ui

import android.os.Bundle
import com.kakakobank.imagecollector.base.component.BaseActivity
import com.kakakobank.imagecollector.ui.favorite.FavoriteFragment
import com.kakakobank.imagecollector.ui.search.SearchFragment
import com.kakakobank.imagecollector.util.Util.setupWithViewPager2
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>() {

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
    private val adapter by lazy { MainTabAdapter(this) }

    override val layoutResId: Int = R.layout.activity_main

    override fun onBindVariable(binding: ActivityMainBinding) {
        binding.adapter = adapter
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.executePendingBindings()
        binding.tabLayout.setupWithViewPager2(
            viewPager = binding.viewPager,
            smoothScroll = false,
            tabText = { _, position -> tabs[position].title }
        )

        adapter.submitList(tabs)
    }
}