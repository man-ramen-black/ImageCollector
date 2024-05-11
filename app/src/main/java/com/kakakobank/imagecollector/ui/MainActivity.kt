package com.kakakobank.imagecollector.ui

import android.os.Bundle
import com.kakakobank.imagecollector.base.component.BaseActivity
import com.kakaobank.imagecollector.R
import com.kakaobank.imagecollector.databinding.ActivityMainBinding

class MainActivity: BaseActivity<ActivityMainBinding>() {

    override val layoutResId: Int = R.layout.activity_main

    override fun onBindVariable(binding: ActivityMainBinding) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}