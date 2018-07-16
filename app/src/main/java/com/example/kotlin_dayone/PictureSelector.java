package com.example.kotlin_dayone;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kotlin_dayone.adapter.PictureAdapter;
import com.example.kotlin_dayone.base.BaseActivity;
import com.example.kotlin_dayone.bean.SelectImgEntity;
import com.example.kotlin_dayone.view.GrideLayoutItemDecoration;
import com.example.kotlin_dayone.view.RecycleViewDivider;

import java.util.ArrayList;

public class PictureSelector extends BaseActivity implements PictureAdapter.CheckboxListener, View.OnClickListener, PictureAdapter.ItemClickListener {

    private RecyclerView mRecyclerView;
    private PictureAdapter mAdapter;
    private ImageView mReturn;

    //跳转预览的请求码
    public static final int QEQUEST_CODE = 1;
    public static final int RESULT_CODE = 2;


    // 带过来的Key
    // 是否显示相机的EXTRA_KEY
    public static final String EXTRA_SHOW_CAMERA = "EXTRA_SHOW_CAMERA";
    // 总共可以选择多少张图片的EXTRA_KEY
    public static final String EXTRA_SELECT_COUNT = "EXTRA_SELECT_COUNT";
    // 原始的图片路径的EXTRA_KEY
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "EXTRA_DEFAULT_SELECTED_LIST";
    // 选择模式的EXTRA_KEY
    public static final String EXTRA_SELECT_MODE = "EXTRA_SELECT_MODE";
    // 返回选择图片列表的EXTRA_KEY
    public static final String EXTRA_RESULT = "EXTRA_RESULT";
    //一行显示多少张图片
    public static final String EXTRA_PICTURE_NUMBER = "EXTRA_PICTURE_NUMBER";

    //多选
    public static final int MODE_MULTI = 0x0011;
    //单选
    public static int MODE_SINGLE = 0x0012;
    //单选或者多选，int类型得type,默认多选
    private int mMode = MODE_MULTI;
    // 加载所有的数据
    private static final int LOADER_TYPE = 0x0021;
    //boolean 是否显示拍照按钮
    private boolean mShowCamera = true;
    //int 类型得图片张数
    private int mMaxCount = 9;
    //ArrayList<String> 选好得图片集合
    private ArrayList<String> mResultList;
    //一行显示多少张图片
    private int mPictureNumber = 4;
    //所有图片的集合
    ArrayList<String> imgs;
    //完成按钮
    private TextView mComplete;
    //预览按钮
    private TextView mPreview;
    //图片预览传过去的集合
    private ArrayList<String> mLookImgs;

    @Override
    public void setContentView() {
        setContentView(R.layout.activity_pictureselector);
    }

    @Override
    public void initView() {
        mRecyclerView = findViewById(R.id.picture_recyclerview);
        mComplete = findViewById(R.id.complete_tv);
        mComplete.setOnClickListener(this);
        mPreview = findViewById(R.id.preview_tv);
        mPreview.setOnClickListener(this);
        mReturn = findViewById(R.id.title_return);
        mReturn.setOnClickListener(this);

    }

    @Override
    public void initData() {
        mLookImgs = new ArrayList<>();

        // 1.获取传递过来的参数
        Intent intent = getIntent();
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, mMode);
        mMaxCount = intent.getIntExtra(EXTRA_SELECT_COUNT, mMaxCount);
        mShowCamera = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, mShowCamera);
        mResultList = (ArrayList<String>) intent.getSerializableExtra(EXTRA_DEFAULT_SELECTED_LIST);
        mPictureNumber = intent.getIntExtra(EXTRA_PICTURE_NUMBER, mPictureNumber);
        if (mResultList == null) {
            mResultList = new ArrayList<>();
        }

        if (mResultList != null && mResultList.size() > 0) {
            mComplete.setText("完成(" + mResultList.size() + "/" + mMaxCount + ")");
            mPreview.setText("预览(" + mResultList.size() + "/" + mMaxCount + ")");
        }

        //初始化本地图片
        initImgList();

    }


    private void initImgList() {
        //耗时操作1.开线程2.AsyncTask
        getLoaderManager().initLoader(LOADER_TYPE, null, mLoaderCallback);
    }

    private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {
        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID};


        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            //查询数据库一样 返回一个游标
            CursorLoader cursorLoader = new CursorLoader(PictureSelector.this,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                    IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[3] + "=? OR "
                            + IMAGE_PROJECTION[3] + "=? ",
                    new String[]{"image/jpeg", "image/png"}, IMAGE_PROJECTION[2] + " DESC");
            return cursorLoader;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            //解析封装到集合
            if (data != null && data.getCount() > 0) {
                imgs = new ArrayList<>();

                while (data.moveToNext()) {
                    imgs.add(data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0])));
                }
                // 显示列表数据
                showImageList(imgs);
            }

        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private void showImageList(ArrayList<String> imgs) {
        mAdapter = new PictureAdapter(this, imgs, this, mResultList, this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, mPictureNumber));
        mRecyclerView.setAdapter(mAdapter);
    }

    //选择按钮点击监听
    @Override
    public void CheckboxClick(int position, boolean checked, ImageView img, CheckBox checkBox) {
        if (checked) {
            if (mResultList.size() >= 9) {
                checkBox.setChecked(false);
                Toast.makeText(PictureSelector.this, "最多选择9张图片", Toast.LENGTH_SHORT).show();
            } else {
                checkBox.setEnabled(true);
                ObjectAnimator(img);
                img.setAlpha(80);
                mResultList.add(imgs.get(position));
                setCompleteButton(mResultList);
            }
        } else {
            checkBox.setEnabled(true);
            ObjectAnimator(img);
            img.setAlpha(0xFF);
            mResultList.remove(imgs.get(position));
            setCompleteButton(mResultList);
        }

    }

    //图片的动画
    private void ObjectAnimator(View target) {
        ObjectAnimator objectOne = ObjectAnimator.ofFloat(target, "scaleX", 1f, 0.7f, 1f);
        ObjectAnimator objectTwo = ObjectAnimator.ofFloat(target, "scaleY", 1f, 0.7f, 1f);
        AnimatorSet set = new AnimatorSet();
        set.play(objectTwo).with(objectOne);
        set.setDuration(350);
        set.start();
    }

    //设置完成 预览 按钮
    public void setCompleteButton(ArrayList<String> list) {
        if (list != null && list.size() > 0) {
            mComplete.setText("完成(" + list.size() + "/" + mMaxCount + ")");
            mPreview.setText("预览(" + list.size() + "/" + mMaxCount + ")");
        } else {
            mComplete.setText("完成");
            mPreview.setText("预览");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.complete_tv:
                if (mResultList != null && mResultList.size() > 0) {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LIST", mResultList);
                    intent.putExtras(bundle);
                    setResult(PictureListActivity.RESULT_CODE, intent);
                    finish();
                }
                break;
            case R.id.title_return:
                finish();
                break;
            case R.id.preview_tv:
                Intent intent = new Intent(PictureSelector.this, LookPictureActivity.class);
                intent.putStringArrayListExtra("LIST", mResultList);
                intent.putStringArrayListExtra("RESULTLIST", mResultList);
                intent.putExtra("MAX", mMaxCount);
                startActivityForResult(intent, RESULT_CODE);
                break;
        }
    }

    //图片的点击监听
    @Override
    public void itemClick(int position) {
        Intent intent = new Intent(PictureSelector.this, LookPictureActivity.class);
        intent.putStringArrayListExtra("LIST", imgs);
        intent.putStringArrayListExtra("RESULTLIST", mResultList);
        intent.putExtra("MAX", mMaxCount);
        intent.putExtra("POSITION", position);
        startActivityForResult(intent, RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_CODE:
                if (data != null) {
                    mResultList = data.getStringArrayListExtra("RESULTLIST");
                    mHandler.sendEmptyMessage(0);
                }
                break;
            case QEQUEST_CODE:
                if (data != null) {
                    mResultList = data.getStringArrayListExtra("RESULTLIST");
                    mHandler.sendEmptyMessage(1);
                }
                break;
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    mAdapter.clear();
                    mAdapter.addList(mResultList);
                    setCompleteButton(mResultList);
                    break;
                case 1:
                    if (mResultList != null) {
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("LIST", mResultList);
                        intent.putExtras(bundle);
                        setResult(PictureListActivity.RESULT_CODE, intent);
                        finish();
                    }
                    break;
            }

        }
    };
}
