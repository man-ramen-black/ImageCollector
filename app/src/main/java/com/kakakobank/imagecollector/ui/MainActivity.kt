package com.kakakobank.imagecollector.ui

import com.kakakobank.imagecollector.base.component.BaseActivity
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity: BaseActivity<ActivityMainBinding>() {
    override val layoutResId: Int = R.layout.activity_main
    override fun onBindVariable(binding: ActivityMainBinding) {}
}