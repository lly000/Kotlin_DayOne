package com.example.kotlin_dayone.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.kotlin_dayone.R;
import com.example.kotlin_dayone.bean.SelectImgEntity;
import com.example.kotlin_dayone.view.SquareImageView;

import java.util.ArrayList;

public class PictureListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<String> mImgs;
    private LayoutInflater mInflater;
    private static final int ITEM_TYPE_ONE = 0x00001;
    private static final int ITEM_TYPE_TWO = 0x00002;
    private addClickListener mListener;
    private DelClickListener mDelListener;


    public PictureListAdapter(Context context, ArrayList<String> list, addClickListener listener
            , DelClickListener delClickListener) {
        this.mContext = context;
        if (list != null && list.size() > 0) {
            this.mImgs = list;
        } else {
            mImgs = new ArrayList<>();
        }
        this.mListener = listener;
        this.mDelListener = delClickListener;
        mInflater = LayoutInflater.from(mContext);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_TWO:
                View oneView = mInflater.inflate(R.layout.adapter_pickter_add_img, null);
                OneHolder oneHolder = new OneHolder(oneView);
                return oneHolder;
            case ITEM_TYPE_ONE:
                View twoView = mInflater.inflate(R.layout.adapter_picture_list, null);
                ViewHolder holder = new ViewHolder(twoView);
                return holder;
            default:
                return null;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ViewHolder) {
            Glide.with(mContext).load(mImgs.get(position)).into(((ViewHolder) holder).imgeview);
            ((ViewHolder) holder).iv_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDelListener.delClick(position);
                }
            });
        } else if (holder instanceof OneHolder) {
            OneHolder oneHolder = (OneHolder) holder;
            if (mImgs.size() >= 9) {
                oneHolder.add_picture.setVisibility(View.GONE);
            } else {
                oneHolder.add_picture.setVisibility(View.VISIBLE);
            }
            oneHolder.add_picture.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    add(mListener);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        if (mImgs != null && mImgs.size() > 0) {
            return mImgs.size() + 1;
        }
        return 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (mImgs != null && mImgs.size() > 0) {
            if (position + 1 == getItemCount()) {
                //加号布局
                return ITEM_TYPE_TWO;
            } else {
                //图片布局
                return ITEM_TYPE_ONE;
            }
        }
        return ITEM_TYPE_TWO;
    }

    public void addList(ArrayList<String> newList) {
        if (newList != null) {
            mImgs.addAll(newList);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        if (mImgs != null && mImgs.size() > 0) {
            mImgs.clear();
        }
    }

    //加号点击监听
    public interface addClickListener {
        public void addClick();
    }

    public void add(addClickListener listener) {
        listener.addClick();
    }

    //删除按钮点击监听
    public interface DelClickListener {
        public void delClick(int position);
    }

    public void delete(DelClickListener listener, int position) {
        listener.delClick(position);
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private SquareImageView imgeview;
        private ImageView iv_del;

        public ViewHolder(View itemView) {
            super(itemView);
            imgeview = itemView.findViewById(R.id.imgeview);
            iv_del = itemView.findViewById(R.id.iv_del);
        }
    }

    class OneHolder extends RecyclerView.ViewHolder {
        private SquareImageView add_picture;

        public OneHolder(View itemView) {
            super(itemView);
            add_picture = itemView.findViewById(R.id.add_picture);
        }
    }
}
