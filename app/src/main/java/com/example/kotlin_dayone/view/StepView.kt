package com.example.kotlin_dayone.view

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.Paint.Join.ROUND
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.kotlin_dayone.R
import java.security.MessageDigest

class StepView : View {
    //次数字体颜色
    private var mStepColor: Int = Color.rgb(255, 255, 255)
    //次数字体大小
    private var mStepSize: Int = 0
    //内部细圆颜色
    private var inCircleColor: Int = Color.rgb(173, 173, 173)
    //外部粗圆的开始颜色
    private var outCircleStartColor: Int = Color.rgb(249, 183, 0)
    //外部圆结束颜色
    private var outCircleEndColor: Int = Color.rgb(246, 144, 5)
    //时间字体颜色
    private var mTimeColor: Int = Color.rgb(255, 255, 255)
    //时间字体的大小
    private var mTimeSize: Int = 10
    //圆的宽高
    private var mWidth: Float = 0f
    private var mHeight: Float = 0f
    //细圆的画笔
    private lateinit var inPaint: Paint
    //细圆的宽度
    private var inRoundWidth: Int = 0
    //粗圆的占比
    private var maxNumber: Float = 100f
    private var nowNumber: Float = 0f
    //粗圆的画笔
    private lateinit var outPaint: Paint
    //粗圆的宽度
    private var outRoundWidth: Int = 10
    //画笔
    private lateinit var mPaint: Paint
    //数量
    private var stepNumber: Int = 0
    //是否更新步数
    private var mIsClick: Boolean = true
    //时间
    private var mTime: String = "00:00:00"
    //渐变数组
    private var RoundColors: IntArray = intArrayOf(Color.rgb(249, 183, 0), Color.rgb(246, 144, 5), Color.rgb(249, 183, 0))


    //继承三个构造方法
    constructor (context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        var array: TypedArray = context!!.obtainStyledAttributes(attrs, R.styleable.StepView)
        mStepColor = array.getColor(R.styleable.StepView_mStepColor, mStepColor)
        mStepSize = array.getDimensionPixelSize(R.styleable.StepView_mStepSize, mStepSize)
        inCircleColor = array.getColor(R.styleable.StepView_inCircleColor, inCircleColor)
        outCircleStartColor = array.getColor(R.styleable.StepView_outCircleStartColor, outCircleStartColor)
        outCircleEndColor = array.getColor(R.styleable.StepView_outCircleEndColor, outCircleEndColor)
        mTimeColor = array.getColor(R.styleable.StepView_mTimeColor, mTimeColor)
        mTimeSize = array.getDimensionPixelSize(R.styleable.StepView_mTimeSize, mTimeSize)
        inRoundWidth = dp2px(context, 2)
        outRoundWidth = dp2px(context, 12)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mPaint = Paint()

        var centerX: Float = mWidth / 2
        var centerY: Float = mHeight / 2

        mPaint.isAntiAlias = true
        mPaint.strokeWidth = inRoundWidth.toFloat()
        mPaint.color = inCircleColor
        mPaint.style = Paint.Style.STROKE
        canvas!!.drawCircle(centerX.toFloat(), centerY.toFloat(),
                (centerX - outRoundWidth).toFloat(), mPaint)

        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = outRoundWidth.toFloat()
        mPaint.color = outCircleEndColor
        mPaint.style=Paint.Style.STROKE
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.strokeJoin = Paint.Join.ROUND
        var radius: Float = centerX - outRoundWidth
        var oval: RectF = RectF(centerX - radius, centerY - radius,
                centerX + radius, centerY + radius)

        var gradient: SweepGradient = SweepGradient(centerX, centerY, RoundColors, null)
        mPaint.shader = gradient

        var percent: Float = nowNumber / maxNumber
        canvas.drawArc(oval, (-90).toFloat(), percent * 360, false, mPaint)

        //画次数
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = mStepColor
        mPaint.textSize = mStepSize.toFloat()
        var mStep = nowNumber.toString()
        var stepx: Int = ((mWidth - mPaint.measureText(stepNumber.toString())) / 2).toInt()
        var fontMetrics: Paint.FontMetrics = mPaint.fontMetrics
        var baseLine: Int = height / 2
        canvas.drawText(stepNumber.toString(), stepx.toFloat(), baseLine.toFloat(), mPaint)

        //画时间
        mPaint.reset()
        mPaint.isAntiAlias = true
        mPaint.color = mTimeColor
        mPaint.textSize = mTimeSize.toFloat()
        var dx: Int = ((width - mPaint.measureText(mTime + "")) / 2).toInt()
        var timeFontMetrics: Paint.FontMetrics = mPaint.fontMetrics
        var h: Int = height / 4
        var timeBaseLine: Int = (h * 3 + (timeFontMetrics.bottom - timeFontMetrics.top) / 2 - timeFontMetrics.bottom).toInt()
        canvas.drawText(mTime, dx.toFloat(), timeBaseLine.toFloat(), mPaint)


    }


    fun dp2px(context: Context, dpValue: Int): Int {
        var scale: Float = context.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    fun setNowNumber(number: Float, click: Boolean) {
        this.nowNumber = number
        this.mIsClick = click
        invalidate()
    }

    //设置次数
    fun setStepNumber(number: Int) {
        this.stepNumber = if (number > 999) 999 else number
    }

    //设置时间
    fun setTime(time: String) {
        this.mTime = time
        invalidate()
    }

}

