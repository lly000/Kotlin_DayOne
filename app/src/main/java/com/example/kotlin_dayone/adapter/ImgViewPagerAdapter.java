package com.example.kotlin_dayone.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kotlin_dayone.view.ZoomImageView;

import java.util.ArrayList;
import java.util.List;

public class ImgViewPagerAdapter extends PagerAdapter {

    private Context mContext;
    private ArrayList<String> mImgs;
    private List<ZoomImageView> mImageViews;

    public ImgViewPagerAdapter(Context context, ArrayList list) {
        this.mContext = context;
        this.mImgs = list;
        mImageViews = new ArrayList<>();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        ZoomImageView imageView = new ZoomImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.MATRIX);
        Glide.with(mContext).load(mImgs.get(position)).into(imageView);
        container.addView(imageView);
        mImageViews.add(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return mImgs.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }
}
