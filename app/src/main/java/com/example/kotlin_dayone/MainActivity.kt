package com.example.kotlin_dayone

import android.Manifest
import android.content.Intent
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.example.kotlin_dayone.base.BaseActivity
import com.example.kotlin_dayone.bean.SelectImgEntity
import com.example.kotlin_dayone.view.StepView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer


class MainActivity : BaseActivity(), View.OnClickListener {

    lateinit var mTitleLeft: TextView
    lateinit var mStart: TextView
    lateinit var mImgList: ArrayList<SelectImgEntity>


    override fun setContentView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.titleColor)
        }
        setContentView(R.layout.activity_main)
        getPermission()
    }

    override fun initView() {
        mTitleLeft = findViewById(R.id.mTitle_left)
        mTitleLeft.setOnClickListener(this)

        mStart = findViewById(R.id.mStart_tv)
        mStart.setOnClickListener(this)


    }

    override fun initData() {
        mImgList = ArrayList()
    }

    //点击监听 Kotlin没有swich case 取而代之的是when表达式
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.mTitle_left -> finish()
            R.id.mStart_tv -> {
                //点击跳转
                var intent: Intent = Intent(this, PictureSelector().javaClass)
                var bundle: Bundle = Bundle();
                bundle.putSerializable(PictureSelector.EXTRA_DEFAULT_SELECTED_LIST, mImgList)
                intent.putExtras(bundle)
                intent.putExtra(PictureSelector.EXTRA_SELECT_COUNT, 9)
                intent.putExtra(PictureSelector.EXTRA_SELECT_MODE, PictureSelector.MODE_MULTI)
                intent.putExtra(PictureSelector.EXTRA_SHOW_CAMERA, false)
                intent.putExtra(PictureSelector.EXTRA_PICTURE_NUMBER, 4)

                startActivity(intent)
            }

        }
    }

    fun getPermission() {
        var rxPermissions: RxPermissions = RxPermissions(this)

        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(Consumer {

                })
    }
}
