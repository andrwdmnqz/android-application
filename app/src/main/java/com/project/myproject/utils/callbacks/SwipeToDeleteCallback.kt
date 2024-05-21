package com.project.myproject.utils.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.data.models.Contact
import com.project.myproject.ui.adapters.ContactAdapter

class SwipeToDeleteCallback(private val swipe: (contact: Contact) -> Unit) :
    ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

        val user = (viewHolder.itemView.parent as RecyclerView).adapter?.let { adapter ->
            if (adapter is ContactAdapter) {
                adapter.currentList[position]
            } else {
                null
            }
        }
        user?.let { swipe(it) }
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}