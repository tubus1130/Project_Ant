package com.onban.kauantapp.common.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.ViewDataBinding

abstract class BaseActivity<VDB: ViewDataBinding> : AppCompatActivity() {
    protected lateinit var binding: VDB

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        inject()
        initBinding()
    }

    private fun initBinding() {
        binding = createBinding()
        setContentView(binding.root)
        binding.lifecycleOwner = this
    }

    protected abstract fun createBinding(): VDB

    protected abstract fun inject()
}