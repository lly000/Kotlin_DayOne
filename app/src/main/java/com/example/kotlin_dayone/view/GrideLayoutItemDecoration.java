package com.example.kotlin_dayone.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.kotlin_dayone.R;

public class GrideLayoutItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public GrideLayoutItemDecoration(Context context, int drawable) {
        mDivider = context.getResources().getDrawable(drawable);
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        //流出分割线的位置，下边和左边
        //如果是最右边不要有，最底部也不要有
        int bottom = mDivider.getIntrinsicHeight();
        int right = mDivider.getIntrinsicWidth();
        if (isLastCloum(view, parent)) {
            right = 0;
        }
        if (isLastRow(view, parent)) {
            bottom = 0;
        }
        outRect.bottom = bottom;
        outRect.right = right;

    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        drawHorizental(c, parent);
        drawVertical(c, parent);
    }

    //最后一行
    private boolean isLastRow(View view, RecyclerView parent) {
        //当前位置  +1> （行数-1）*列数
        int spanCount = getSpanCount(parent);

        int rowNumber = parent.getAdapter().getItemCount() % spanCount == 0 ?
                parent.getAdapter().getItemCount() / spanCount : (parent.getAdapter().getItemCount() / spanCount + 1);

        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

        return (currentPosition + 1) > (rowNumber - 1) * spanCount;
    }

    //最后一列
    private boolean isLastCloum(View view, RecyclerView parent) {
        //获取当前位置
        int currentPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        //获取列数
        int spanCount = getSpanCount(parent);
        return (currentPosition + 1) % spanCount == 0;
    }

    private int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager) layoutManager;
            int spanCount = gridLayoutManager.getSpanCount();
            return spanCount;
        }
        return 1;
    }

    //绘制垂直分割线
    private void drawVertical(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int top = childView.getTop() - params.topMargin;
            int bottom = childView.getBottom() + params.bottomMargin;
            int left = childView.getRight() + params.rightMargin;
            int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    //绘制水平方向的分割线
    private void drawHorizental(Canvas c, RecyclerView parent) {
        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = parent.getChildAt(i);
            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) childView.getLayoutParams();
            int left = childView.getLeft() - params.leftMargin;
            int right = childView.getRight() + mDivider.getIntrinsicWidth() + params.rightMargin;
            int top = childView.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }

    }
}
