package com.example.kotlin_dayone;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.kotlin_dayone.adapter.PictureAdapter;
import com.example.kotlin_dayone.adapter.PictureListAdapter;
import com.example.kotlin_dayone.base.BaseActivity;
import com.example.kotlin_dayone.bean.SelectImgEntity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.functions.Consumer;

public class PictureListActivity extends BaseActivity implements View.OnClickListener, PictureListAdapter.addClickListener
        , PictureListAdapter.DelClickListener {

    //图片集合
    private ArrayList<String> mImgList;
    public static int REQUEST_CODE = 1;
    public static int RESULT_CODE = 2;
    private RecyclerView mRecyclerView;
    private PictureListAdapter mAdapter;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_picture_list);
        getPermission();
    }


    @Override
    public void initView() {
        mRecyclerView = findViewById(R.id.recycler_img);

    }

    @Override
    public void initData() {
        mImgList = new ArrayList();

        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mAdapter = new PictureListAdapter(this, mImgList, this, this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_picture:

                break;
        }
    }

    //获取权限
    private void getPermission() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
                , Manifest.permission.ACCESS_NETWORK_STATE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case 2:
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    mImgList = (ArrayList<String>) bundle.getSerializable("LIST");
                    mHandler.sendEmptyMessage(0);
                }
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            mAdapter.clear();
            mAdapter.addList(mImgList);
        }
    };


    //加号点击监听
    @Override
    public void addClick() {
        Intent intent = new Intent(PictureListActivity.this, PictureSelector.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(PictureSelector.EXTRA_DEFAULT_SELECTED_LIST, mImgList);
        intent.putExtras(bundle);
        intent.putExtra(PictureSelector.EXTRA_SELECT_COUNT, 9);
        intent.putExtra(PictureSelector.EXTRA_SELECT_MODE, PictureSelector.MODE_MULTI);
        intent.putExtra(PictureSelector.EXTRA_SHOW_CAMERA, true);
        intent.putExtra(PictureSelector.EXTRA_PICTURE_NUMBER, 4);
        startActivityForResult(intent, REQUEST_CODE);
    }


    //删除按钮点击监听
    @Override
    public void delClick(int position) {
        mImgList.remove(position);
        mAdapter.clear();
        mAdapter.addList(mImgList);
    }
}
