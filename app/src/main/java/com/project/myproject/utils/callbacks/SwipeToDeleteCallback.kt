package com.project.myproject.utils.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.ui.adapters.UserAdapter
import com.project.myproject.data.models.User

class SwipeToDeleteCallback(private val swipe: (position: Int, user: User) -> Unit) :
    ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val user = (viewHolder.itemView.parent as RecyclerView).adapter?.let { adapter ->
            if (adapter is UserAdapter) {
                adapter.currentList[position]
            } else {
                null
            }
        }
        user?.let { swipe(position, it) }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}