package com.kakakobank.imagecollector.ui.main

import androidx.fragment.app.Fragment
import com.kakakobank.imagecollector.base.adapter.BaseFragmentStateAdapter

/**
 * [MainFragment]
 */
class MainTab(val title: String, val createFragment: () -> Fragment)

class MainTabAdapter(fragment: Fragment): BaseFragmentStateAdapter<MainTab>(fragment) {
    override fun createFragment(position: Int): Fragment {
        return getItem(position).createFragment()
    }
}