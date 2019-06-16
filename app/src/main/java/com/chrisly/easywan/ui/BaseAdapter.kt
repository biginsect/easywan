package com.chrisly.easywan.ui

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chrisly.easywan.util.ListUtils

/**
 * @author big insect
 * @date 2019/6/16.
 */
abstract class BaseAdapter<VH: BaseViewHolder, D: Any>(private val context: Context)
    : RecyclerView.Adapter<VH>(),
    BaseViewHolder.OnItemClickListener, BaseViewHolder.OnItemLongClickListener {

    internal lateinit var dataList: ArrayList<D>
    internal var onItemClickListener: BaseViewHolder.OnItemClickListener? = null
    internal var onItemLongClickListener: BaseViewHolder.OnItemLongClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(getLayoutId(), parent, false)
        return getViewHolder(view, viewType)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        if (null != onItemClickListener) {
            holder.onItemClickListener = this
        }
        if (null != onItemLongClickListener) {
            holder.onItemLongClickListener = this
        }
    }

    override fun onItemClick(position: Int, view: View) {
        onItemClickListener?.onItemClick(position, view)
    }

    override fun onItemLongClick(position: Int, view: View): Boolean {
        if (null != onItemLongClickListener){
            return onItemLongClickListener!!.onItemLongClick(position, view)
        }

        return false
    }

    override fun getItemCount(): Int {
        return ListUtils.getSize(dataList)
    }

    /**
     * 获取到数据，通知视图更新
     * */
    fun setData(dataList: ArrayList<D>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    /**
     * 获取布局ID
     * */
    protected abstract fun getLayoutId(): Int

    /**
     * 构建ViewHolder
     * */
    protected abstract fun getViewHolder(itemView: View, viewType: Int): VH
}