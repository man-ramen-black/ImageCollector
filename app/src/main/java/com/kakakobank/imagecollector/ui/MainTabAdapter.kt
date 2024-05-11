package com.kakakobank.imagecollector.ui

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.kakakobank.imagecollector.base.adapter.BaseFragmentStateAdapter

/**
 * [MainActivity]
 */
class MainTab(val title: String, val createFragment: () -> Fragment)

class MainTabAdapter(activity: FragmentActivity): BaseFragmentStateAdapter<MainTab>(activity) {
    override fun createFragment(position: Int): Fragment {
        return getItem(position).createFragment()
    }
}