package com.example.kotlin_dayone

import android.content.ClipData
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.adapter_item_layout.view.*

class MyAdapter : RecyclerView.Adapter<MyAdapter.ViewHolder> {
    var mContext: Context
    var mDatas: List<String>
    var mInflater: LayoutInflater

    constructor(context: Context, list: List<String>) {
        this.mContext = context
        this.mDatas = list
        mInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = mInflater.inflate(R.layout.adapter_item_layout, null)
        val holder: ViewHolder = ViewHolder(itemView)
        return holder
    }

    override fun getItemCount(): Int {
        if (mDatas.size > 0 && mDatas != null) {
            return mDatas.size
        }
        return 0
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.text.text = mDatas[position]
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var text: TextView = itemView.findViewById(R.id.text_adapter)
    }
}