package com.project.myproject.utils.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.myproject.data.models.User

class UserItemDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}