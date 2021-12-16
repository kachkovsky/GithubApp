package com.github.kachkovsky.githubapp.ui.base

import androidx.recyclerview.widget.RecyclerView

abstract class BaseListRecyclerAdapter<L : List<*>, VH : RecyclerView.ViewHolder> :
    RecyclerView.Adapter<VH>() {
    var items: L? = null
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }
}