package com.github.kachkovsky.githubapp.ui.utils

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessScrollListener : RecyclerView.OnScrollListener {

    private var layoutManager: LinearLayoutManager
    private var visibleThreshold = 5
    private var loading = true
    private val previousItemCount = 0

    constructor(layoutManager: LinearLayoutManager) {
        this.layoutManager = layoutManager
    }

    constructor(layoutManager: LinearLayoutManager, visibleThreshold: Int) {
        this.visibleThreshold = visibleThreshold
        this.layoutManager = layoutManager
    }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        if (dy > 0) {
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount
            val pastVisibleItems = layoutManager.findFirstVisibleItemPosition()
            if (loading) {
                loading = previousItemCount == totalItemCount
            } else {
                if (visibleItemCount + pastVisibleItems + visibleThreshold >= totalItemCount) {
                    loading = onLoadMore()
                }
            }
        }
    }

    abstract fun onLoadMore(): Boolean
}