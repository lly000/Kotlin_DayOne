package com.example.kotlin_dayone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotlin_dayone.adapter.ImgViewPagerAdapter;
import com.example.kotlin_dayone.base.BaseActivity;
import com.example.kotlin_dayone.view.ZoomImageView;

import java.util.ArrayList;

public class LookPictureActivity extends BaseActivity implements View.OnClickListener {

    private ViewPager mViewPager;
    //所有图片的集合
    private ArrayList<String> mImgs;
    //选中图片的集合
    private ArrayList<String> mResultList;
    private ImgViewPagerAdapter mAdapter;
    //要预览的图片的位置
    private int position;
    //当前图片的位置
    private int currentPosition;
    //最多可选择多少张图片
    private int mMaxPicture;

    private TextView mTitleReturn;
    private CheckBox mCheckBox;
    private TextView mComplete;


    @Override
    public void setContentView() {
        setContentView(R.layout.activity_lookpicture);
    }

    @Override
    public void initView() {
        mViewPager = findViewById(R.id.viewpager);
        mTitleReturn = findViewById(R.id.title_return);
        mTitleReturn.setOnClickListener(this);
        mCheckBox = findViewById(R.id.checkbox);
        mCheckBox.setOnClickListener(this);
        mComplete = findViewById(R.id.complete_tv);
        mComplete.setOnClickListener(this);
    }

    @Override
    public void initData() {
        Intent intent = getIntent();
        mImgs = intent.getStringArrayListExtra("LIST");
        position = intent.getIntExtra("POSITION", 0);
        mResultList = intent.getStringArrayListExtra("RESULTLIST");
        mMaxPicture = intent.getIntExtra("MAX", 9);


        mAdapter = new ImgViewPagerAdapter(this, mImgs);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(position);
        currentPosition = position;
        mTitleReturn.setText((position + 1) + "/" + mImgs.size());
        if (mResultList.size() > 0) {
            mComplete.setText("完成" + "(" + mResultList.size() + "/" + mMaxPicture + ")");
        }


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                currentPosition = position;
                mTitleReturn.setText((position + 1) + "/" + mImgs.size());
                String path = mImgs.get(position);
                if (mResultList.contains(path)) {
                    mCheckBox.setChecked(true);
                } else {
                    mCheckBox.setChecked(false);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_return:
                Intent intent = new Intent();
                intent.putStringArrayListExtra("RESULTLIST", mResultList);
                setResult(PictureSelector.RESULT_CODE, intent);
                finish();
                break;
            case R.id.checkbox:
                if (mCheckBox.isChecked()) {
                    if (mResultList.size() < 9) {
                        mResultList.add(mImgs.get(currentPosition));
                    } else {
                        mCheckBox.setChecked(false);
                        Toast.makeText(LookPictureActivity.this, "最多选择" + mMaxPicture + "张图片", Toast.LENGTH_SHORT).show();
                    }
                    mComplete.setText("完成" + "(" + mResultList.size() + "/" + mMaxPicture + ")");
                } else {
                    mResultList.remove(mImgs.get(currentPosition));
                    if (mResultList.size() > 0) {
                        mComplete.setText("完成" + "(" + mResultList.size() + "/" + mMaxPicture + ")");
                    } else {
                        mComplete.setText("完成");
                    }

                }
                break;
            case R.id.complete_tv:
                if (mResultList.size() > 0) {
                    Intent complete = new Intent();
                    complete.putStringArrayListExtra("RESULTLIST", mResultList);
                    setResult(PictureSelector.QEQUEST_CODE, complete);
                    finish();
                } else {
                    Toast.makeText(LookPictureActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putStringArrayListExtra("RESULTLIST", mResultList);
            setResult(PictureSelector.RESULT_CODE, intent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
}
