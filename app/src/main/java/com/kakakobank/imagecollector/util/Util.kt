package com.kakakobank.imagecollector.util

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.withStateAtLeast
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

object Util {
    /**
     * 특정 lifecycle 시점에 로직 실행
     */
    fun launchWhenState(owner : LifecycleOwner, state: Lifecycle.State, runnable: () -> Unit): Job {
        return owner.lifecycleScope.launch {
            owner.lifecycle.withStateAtLeast(state, runnable)
        }
    }

    /**
     * TabLayout과 ViewPager2 연결
     */
    fun TabLayout.setupWithViewPager2(viewPager: ViewPager2, autoRefresh: Boolean = true, smoothScroll: Boolean = true, tabText: ((TabLayout.Tab, Int) -> String)? = null) {
        TabLayoutMediator(this, viewPager, autoRefresh, smoothScroll) { tab, position ->
            tab.text = tabText?.invoke(tab, position) ?: ""
        }.attach()
    }
}