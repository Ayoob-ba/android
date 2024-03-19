package com.tonapps.uikit.list

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

abstract class BaseListAdapter: ListAdapter<BaseListItem, BaseListHolder<out BaseListItem>>(
    DiffCallback.create()
) {

    private var recyclerViewRef: RecyclerView? = null

    override fun submitList(list: List<BaseListItem>?) {
        submitList(list, null)
    }

    override fun submitList(list: List<BaseListItem>?, commitCallback: Runnable?) {
        super.submitList(list, commitCallback)
    }

    abstract fun createHolder(parent: ViewGroup, viewType: Int): BaseListHolder<out BaseListItem>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseListHolder<out BaseListItem> {
        return createHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: BaseListHolder<out BaseListItem>, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).type
    }

    public override fun getItem(position: Int): BaseListItem {
        return currentList[position]
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        recyclerViewRef = null
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerViewRef = recyclerView
        recyclerView.setHasFixedSize(true)
        recyclerView.isNestedScrollingEnabled = false
        recyclerView.itemAnimator = null
        recyclerView.layoutAnimation = null
    }
}