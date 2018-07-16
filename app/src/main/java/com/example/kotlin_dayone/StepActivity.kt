package com.example.kotlin_dayone

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Build
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.DecelerateInterpolator
import com.example.kotlin_dayone.base.BaseActivity
import com.example.kotlin_dayone.view.StepView

class StepActivity : BaseActivity(), View.OnClickListener {


    lateinit var mStepView: StepView
    var number: Int = 0
    //开始值
    var start: Int = 0
    //终止值
    var end: Int = 100
    //判断是否再次点击
    var isClick: Boolean = true

    override fun setContentView() {
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ContextCompat.getColor(this, R.color.titleColor)
        }
        setContentView(R.layout.activity_step)
    }

    override fun initView() {
        mStepView = findViewById(R.id.mStepView)
        mStepView.setOnClickListener(this)
    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        when (v!!.id) {
            R.id.mStepView -> {
                number++
                var animator: ValueAnimator = ObjectAnimator.ofFloat(start.toFloat(), end.toFloat())
                animator.duration = 500
                animator.setInterpolator(DecelerateInterpolator())
                animator.addUpdateListener {
                    var value: Float = it.animatedValue as Float
                    if (value.toInt() < 100) {
                        isClick = false
                        mStepView.setNowNumber(value, isClick)
                    } else {
                        isClick = true;
                        mStepView.setNowNumber(start.toFloat(), isClick);
                    }
                }
                animator.start();
                mStepView.setStepNumber(number)
            }
        }
    }

}