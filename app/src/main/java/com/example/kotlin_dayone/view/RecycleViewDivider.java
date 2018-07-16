package com.example.kotlin_dayone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.kotlin_dayone.R;

public class RecycleViewDivider extends RecyclerView.ItemDecoration {
    private Paint mPaint;

    private Drawable mDivider;
    //分割线缩进值
    private int inset;
    //
    private int mOrientation;

    /**
     * @param context
     * @param drawable    引入的drawable id
     * @param ins         分割线缩进值
     */
    public RecycleViewDivider(Context context, int drawable, int ins) {
        mDivider = context.getResources().getDrawable(drawable);
        mPaint = new Paint();
        mPaint.setColor(context.getResources().getColor(R.color.white));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            drawVertical(c, parent);
    }


    private void drawVertical(Canvas c, RecyclerView parent) {
        int left = parent.getPaddingLeft();
        int right = parent.getPaddingRight();
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            if (inset > 0) {
                c.drawRect(left, top, right, bottom, mPaint);
                mDivider.setBounds(left + inset, top, right - inset, bottom);
            } else {
                mDivider.setBounds(left, top, right, bottom);
            }
            mDivider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
