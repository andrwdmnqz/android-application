package com.project.myproject.ui.fragments.utils

import androidx.recyclerview.widget.RecyclerView

class CustomAdapterDataObserver(private val recyclerView: RecyclerView): RecyclerView.AdapterDataObserver() {
    override fun onChanged() {
        // No need to scroll here
    }
    override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
        // No need to scroll here
    }
    override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
        // No need to scroll here
    }
    override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
        if (itemCount == 1 && positionStart == 0) {
            recyclerView.scrollToPosition(positionStart)
        }
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
        // No need to scroll here
    }
    override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
        // No need to scroll here
    }
}