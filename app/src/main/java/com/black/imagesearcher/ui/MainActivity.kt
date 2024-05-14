package com.black.imagesearcher.ui

import com.black.imagesearcher.base.component.BaseActivity
import com.black.imagesearcher.R
import com.black.imagesearcher.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int = R.layout.activity_main
    override fun onBindVariable(binding: ActivityMainBinding) {}
}