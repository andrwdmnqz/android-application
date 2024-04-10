package com.project.myproject.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.models.User
import com.project.myproject.viewmodels.UserViewModel

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