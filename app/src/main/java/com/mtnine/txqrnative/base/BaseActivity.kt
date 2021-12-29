package com.mtnine.txqrnative.base

import android.os.Bundle
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.mtnine.txqrnative.BR

abstract class BaseActivity<B : ViewDataBinding, VM : BaseViewModel>(@LayoutRes private val layoutResId: Int) :
    AppCompatActivity() {
    lateinit var binding: B
    abstract val viewModel: VM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, layoutResId)
        binding.setVariable(BR.vm, viewModel)
        binding.lifecycleOwner = this
    }

    fun showToast(resource: Int) =
        Toast.makeText(applicationContext, getString(resource), Toast.LENGTH_SHORT).show()

    fun showToast(msg: String) =
        Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
}