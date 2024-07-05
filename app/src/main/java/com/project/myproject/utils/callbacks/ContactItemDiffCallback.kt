package com.project.myproject.utils.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.project.myproject.data.room.entities.Contact

class ContactItemDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean {
        return oldItem == newItem
    }
}