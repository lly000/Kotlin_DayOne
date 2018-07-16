package com.example.kotlin_dayone.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

public class ZoomImageView extends ImageView implements ViewTreeObserver.OnGlobalLayoutListener
        , ScaleGestureDetector.OnScaleGestureListener, View.OnTouchListener {

    private boolean mOnce = false;

    //初始化的缩放值x
    private float mInitScaleX;
    //初始化缩放值y
    private float mInitScaleY;
    //初始化缩放值
    private float mInitScale;

    //双击放大值到达的值
    private float mMidScaleX;
    private float mMidScaleY;

    //放大的最大值
    private float mMaxScaleX;
    private float mMaxScaleY;

    private Matrix mMatrix;
    //捕获用户多点触碰时缩放的比例
    private ScaleGestureDetector mScaleGestureDetector;

    //自由移动
    //记录上一次多点触碰的数量
    private int mLastPointerCount;
    private float mLastX;
    private float mLastY;
    private int mTouchSlop;
    private boolean isCanDrag;

    private boolean isCheckLeftAndRight;
    private boolean isCheckTopAndBottom;

    //双击放大与缩小
    private GestureDetector mGestureDetector;
    private boolean isAutoScale;



    public ZoomImageView(Context context) {
        this(context, null);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZoomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //初始化
        mMatrix = new Matrix();
        mScaleGestureDetector = new ScaleGestureDetector(context, this);
        setOnTouchListener(this);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();

        mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {

                if (isAutoScale) {
                    return true;
                }
                float x = e.getX();
                float y = e.getY();
                if (getScale()[0] < mMidScaleX && getScale()[1] < mMidScaleY) {
                    postDelayed(new AutoScaleRunnable(mMidScaleX,mMidScaleY, x, y), 16);
                    isAutoScale = true;
                } else {
                    postDelayed(new AutoScaleRunnable(mInitScaleX,mInitScaleY, x, y), 16);
                    isAutoScale = true;
                }

                return true;
            }
        });
    }

    //自动放大与缩小
    private class AutoScaleRunnable implements Runnable {

        //缩放的目标值
        private float mTargetScaleX;
        private float mTargetScaleY;

        private float x;
        private float y;

        private final float BIGGER = 1.07f;
        private final float SMALL = 0.93f;

        private float tmpScale;

        public AutoScaleRunnable(float mTargetScaleX, float mTargetScaleY, float x, float y) {
            this.mTargetScaleX = mTargetScaleX;
            this.mTargetScaleY = mTargetScaleY;
            this.x = x;
            this.y = y;
            if (getScale()[0] < mTargetScaleX && getScale()[1] < mTargetScaleY) {
                tmpScale = BIGGER;
            } else {
                tmpScale = SMALL;
            }
        }

        @Override
        public void run() {
            //进行缩放
            mMatrix.postScale(tmpScale, tmpScale, x, y);
            checkBorderAndCenter();
            setImageMatrix(mMatrix);
            float currentScaleX = getScale()[0];
            float currentScaleY = getScale()[1];
            if ((tmpScale > 1.0f) && currentScaleX < mTargetScaleX && currentScaleY < mTargetScaleY
                    || (tmpScale < 1.0f) && currentScaleX > mTargetScaleX && currentScaleY > mTargetScaleY) {

                postDelayed(this, 16);
            } else {
                float scaleX = mTargetScaleX / currentScaleX;
                float scaleY = mTargetScaleY / currentScaleY;
                mMatrix.postScale(scaleX, scaleY, x, y);
                checkBorderAndCenter();
                setImageMatrix(mMatrix);
                isAutoScale = false;
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        getViewTreeObserver().addOnGlobalLayoutListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        getViewTreeObserver().removeOnGlobalLayoutListener(this);
    }

    /**
     * 获取imageView加载完成的图片
     */
    @Override
    public void onGlobalLayout() {
        if (!mOnce) {
            //得到控件的宽和高
            int width = getWidth();
            int height = getHeight();
//            Log.e("图片的控件的宽高", "width--->" + width + "height--->" + height);
            //得到图片，以及宽高
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return;
            }

            int imgWidth = drawable.getIntrinsicWidth();
            int imgHeight = drawable.getIntrinsicHeight();

            //缩放比值
            float scaleX = 1.0f;
            float scaleY = 1.0f;
            /**
             * 如果图片的宽度大于控件的宽，高度小于控件的高度，将图片缩小
             */
            scaleX = width * 1.0f / imgWidth;
            scaleY = height * 1.0f / imgHeight;

            //得到初始化时缩放的比例
            mInitScaleX = scaleX;
            mInitScaleY = scaleY;
            mMidScaleX = mInitScaleX * 2;
            mMidScaleY = mInitScaleY * 2;
            mMaxScaleX = mInitScaleX * 4;
            mMaxScaleY = mInitScaleY * 4;

            //将图片移动至控件的中心
            //图片移动的x坐标值
            int dx = getWidth() / 2 - imgWidth / 2;
            //图片移动的y坐标值
            int dy = getHeight() / 2 - imgHeight / 2;

            //平移图片
            mMatrix.postTranslate(dx, dy);
            //缩放图片
            mMatrix.postScale(scaleX, scaleY, width / 2, height / 2);
            setImageMatrix(mMatrix);

            mOnce = true;
        }
    }

    /**
     * 获取当前图片的缩放值
     *
     * @return
     */
    public float[] getScale() {
        float[] values = new float[9];
        mMatrix.getValues(values);
        return new float[]{values[Matrix.MSCALE_X], values[Matrix.MSCALE_Y]};
    }


    //缩放中
    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        float scaleX = getScale()[0];
        float scaleY = getScale()[1];
        //获得缩放的值
        float scaleFactorX = detector.getScaleFactor();
        float scaleFactorY = detector.getScaleFactor();
        if (getDrawable() == null) {
            return true;
        }
        //缩放范围的控制
        if ((scaleX < mMaxScaleX && scaleY < mMidScaleY && scaleFactorX > 1.0f)
                || (scaleX > mInitScaleX && scaleY > mInitScaleY && scaleFactorX < 1.0f)) {
            if (scaleX * scaleFactorX < mInitScaleX) {
                scaleFactorX = mInitScaleX / scaleX;
            }
            if (scaleX * scaleFactorX > mMaxScaleX) {
                scaleX = mMaxScaleX / scaleX;
            }
            if (scaleY * scaleFactorY < mInitScaleY) {
                scaleFactorY = mInitScaleY / scaleY;
            }
            if (scaleY * scaleFactorY > mMaxScaleY) {
                scaleY = mMaxScaleY / scaleY;
            }

            mMatrix.postScale(scaleFactorX, scaleFactorY, detector.getFocusX(), detector.getFocusY());

            checkBorderAndCenter();

            setImageMatrix(mMatrix);
        }
        return true;
    }

    /**
     * 获得图片放大缩小以后的图标
     *
     * @return
     */
    private RectF getMatrixRectF() {
        Matrix matrix = mMatrix;
        RectF rectF = new RectF();
        Drawable drawable = getDrawable();
        if (drawable != null) {
            rectF.set(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
            matrix.mapRect(rectF);
        }
        return rectF;
    }


    /**
     * 缩放的时候进行边界控制以及位置的控制
     */
    private void checkBorderAndCenter() {
        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

        if (rect.width() >= width) {
            //左边有空隙
            if (rect.left > 0) {
                deltaX = -rect.left;
            }
            if (rect.right < width) {
                deltaX = width - rect.right;
            }
        }
        if (rect.height() >= height) {
            if (rect.top > 0) {
                deltaY = -rect.top;
            }
            if (rect.bottom < height) {
                deltaY = height - rect.bottom;
            }
        }
        //如果宽度或者高度小于控件的宽度或者高度，剧中
        if (rect.width() < width) {
            deltaX = width / 2 - rect.right + rect.width() / 2;
        }
        if (rect.height() < height) {
            deltaY = height / 2 - rect.bottom + rect.height() / 2;
        }

        mMatrix.postTranslate(deltaX, deltaY);

    }

    //开始缩放
    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        return true;
    }

    //结束缩放
    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {

    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (mGestureDetector.onTouchEvent(event)) {
            return true;
        }

        mScaleGestureDetector.onTouchEvent(event);

        float x = 0;
        float y = 0;

        //拿到多点触碰的数量
        int pointerCount = event.getPointerCount();
        for (int i = 0; i < pointerCount; i++) {
            x += event.getX(i);
            y += event.getY(i);
        }

        x /= pointerCount;
        y /= pointerCount;

        if (mLastPointerCount != pointerCount) {
            isCanDrag = false;
            mLastX = x;
            mLastY = y;
        }
        mLastPointerCount = pointerCount;

        RectF abc = getMatrixRectF();
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                if (abc.width() > getWidth() + 0.01 || abc.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;

            case MotionEvent.ACTION_MOVE:

                if (abc.width() > getWidth() + 0.01 || abc.height() > getHeight() + 0.01) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                float dx = x - mLastX;
                float dy = y - mLastY;

                if (!isCanDrag) {
                    isCanDrag = isMoveAction(dx, dy);
                }

                if (isCanDrag) {
                    RectF rectF = getMatrixRectF();
                    if (getDrawable() != null) {
                        isCheckLeftAndRight = isCheckTopAndBottom = true;
                        //如果宽度小于控件宽度不允许横向移动
                        if (rectF.width() < getWidth()) {
                            isCheckLeftAndRight = false;
                            dx = 0;
                        }
                        //如果高度下雨控件高度不允许纵向移动
                        if (rectF.height() < getHeight()) {
                            isCheckTopAndBottom = false;
                            dy = 0;
                        }

                        mMatrix.postTranslate(dx, dy);
                        checkBorderWhenTranslate();
                        setImageMatrix(mMatrix);
                    }
                }

                mLastX = x;
                mLastY = y;

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mLastPointerCount = 0;

                break;
        }

        return true;
    }

    /**
     * 当移动时进行边界检查
     */
    private void checkBorderWhenTranslate() {

        RectF rectF = getMatrixRectF();
        float deltaX = 0;
        float deltay = 0;

        int width = getWidth();
        int height = getHeight();

        if (rectF.top > 0 && isCheckTopAndBottom) {
            deltay = -rectF.top;
        }
        if (rectF.bottom < height && isCheckTopAndBottom) {
            deltay = height - rectF.bottom;
        }
        if (rectF.left > 0 && isCheckLeftAndRight) {
            deltaX = -rectF.left;
        }
        if (rectF.right < width && isCheckLeftAndRight) {
            deltaX = width - rectF.right;
        }
        mMatrix.postTranslate(deltaX, deltay);
    }

    /**
     * 判断是否move
     *
     * @param dx
     * @param dy
     * @return
     */
    private boolean isMoveAction(float dx, float dy) {

        return Math.sqrt(dx * dx + dy * dy) > mTouchSlop;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
