package com.example.kotlin_dayone.adapter;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.bumptech.glide.Glide;
import com.example.kotlin_dayone.R;
import com.example.kotlin_dayone.bean.SelectImgEntity;
import com.example.kotlin_dayone.view.SquareImageView;

import java.util.ArrayList;

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder> {

    private Context mContext;
    //所有图片的集合
    private ArrayList<String> imgs;
    private LayoutInflater mInflater;
    // 被选中的图片的集合
    private ArrayList<String> mSelectList;
    //选择按钮点击监听
    private CheckboxListener mCheckboxListener;
    //图片点击监听
    private ItemClickListener itemClickListener;


    public PictureAdapter(Context context, ArrayList<String> entity, CheckboxListener checkboxListener
            , ArrayList<String> selectImgs, ItemClickListener listener) {
        this.mContext = context;
        this.imgs = entity;
        this.mCheckboxListener = checkboxListener;
        this.itemClickListener = listener;
        this.mSelectList = selectImgs;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.adapter_picture, null);
        ViewHolder holder = new ViewHolder(itemView);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        holder.camera_ll.setVisibility(View.GONE);
        holder.media_selected_indicator.setVisibility(View.VISIBLE);
        holder.image.setVisibility(View.VISIBLE);
        // 显示图片利用Glide
        Glide.with(mContext).load(imgs.get(position))
                .centerCrop().into(holder.image);

        holder.media_selected_indicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isChecked = ((CheckBox) v).isChecked();
                onCheckboxClick(mCheckboxListener, position, isChecked, holder.image, holder.media_selected_indicator);
            }
        });

        //图片点击监听
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClick(itemClickListener, position);
            }
        });

        for (int i = 0; i < mSelectList.size(); i++) {
            if (mSelectList.get(i).toString().equals(imgs.get(position).toString())) {
                holder.media_selected_indicator.setChecked(true);
                holder.image.setAlpha(80);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imgs.size();
    }

    public void addList(ArrayList<String> newList) {
        if (newList != null) {
            mSelectList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void clear() {
        mSelectList.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout camera_ll;
        private CheckBox media_selected_indicator;
        private ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
            camera_ll = itemView.findViewById(R.id.camera_ll);
            media_selected_indicator = itemView.findViewById(R.id.media_selected_indicator);
            image = itemView.findViewById(R.id.image);
        }
    }

    //条目点击监听
    public interface ItemClickListener {
        public void itemClick(int position);
    }

    public void onItemClick(ItemClickListener listener, int position) {
        listener.itemClick(position);
    }


    //选择按钮点击监听
    public interface CheckboxListener {
        public void CheckboxClick(int position, boolean checked, ImageView img, CheckBox checkBox);
    }

    public void onCheckboxClick(CheckboxListener listener, int position, boolean checked, ImageView img, CheckBox cb) {
        listener.CheckboxClick(position, checked, img, cb);
    }


}
