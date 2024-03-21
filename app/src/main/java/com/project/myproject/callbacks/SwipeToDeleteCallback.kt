package com.project.myproject.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.adapters.UserAdapter

class SwipeToDeleteCallback(private val adapter: UserAdapter) :
    ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        adapter.deleteItem(position, viewHolder as UserAdapter.ViewHolder)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}