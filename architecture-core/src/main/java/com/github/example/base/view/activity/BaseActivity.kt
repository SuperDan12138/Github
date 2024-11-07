package com.github.example.base.view.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.example.base.view.IView

abstract class BaseActivity : AppCompatActivity(), com.github.example.base.view.IView {

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }
}
