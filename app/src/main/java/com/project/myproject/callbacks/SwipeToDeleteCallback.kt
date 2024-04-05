package com.project.myproject.callbacks

import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.viewmodels.UserViewModel

class SwipeToDeleteCallback(private val viewModel: UserViewModel, private val recyclerView: RecyclerView) :
    ItemTouchHelper.SimpleCallback(0,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        val user = (viewHolder as UserAdapter.ViewHolder).user
        viewModel.deleteUser(user)

        val snackbar = Snackbar.make(recyclerView,
            recyclerView.context.getString(R.string.contact_removed), Snackbar.LENGTH_LONG)

        snackbar.setAction(recyclerView.context.getString(R.string.undo_button)) {
            viewModel.addUser(position, user)
            recyclerView.scrollToPosition(position)
        }
        snackbar.show()
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }
}