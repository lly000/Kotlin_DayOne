package com.example.kotlin_dayone.base

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //设置布局
        setContentView()
        initView()
        initData()
    }

    //设置布局
    abstract fun setContentView()

    //家在布局
    abstract fun initView()

    //设置数据
    abstract fun initData()


}