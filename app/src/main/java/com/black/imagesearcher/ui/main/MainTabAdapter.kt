package com.black.imagesearcher.ui.main

import androidx.fragment.app.Fragment
import com.black.imagesearcher.base.adapter.BaseFragmentStateAdapter

/**
 * [MainFragment]
 */
class MainTab(val title: String, val createFragment: () -> Fragment)

class MainTabAdapter(fragment: Fragment): BaseFragmentStateAdapter<MainTab>(fragment) {
    override fun createFragment(position: Int): Fragment {
        return getItem(position).createFragment()
    }
}