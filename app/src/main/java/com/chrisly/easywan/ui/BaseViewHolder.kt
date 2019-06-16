package com.chrisly.easywan.ui

import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * @author big insect
 * @date 2019/6/16.
 */
open class BaseViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
    ,View.OnClickListener, View.OnLongClickListener {

    lateinit var onItemClickListener: OnItemClickListener
    lateinit var onItemLongClickListener: OnItemLongClickListener

    override fun onClick(v: View) {
        if (adapterPosition != RecyclerView.NO_POSITION){
            onItemClickListener.onItemClick(adapterPosition, v)
        }
    }

    override fun onLongClick(v: View): Boolean {
        return adapterPosition != RecyclerView.NO_POSITION && onItemLongClickListener.onItemLongClick(adapterPosition, v)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int, view: View): Boolean
    }
}