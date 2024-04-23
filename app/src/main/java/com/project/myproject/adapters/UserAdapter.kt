package com.project.myproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.Constants
import com.project.myproject.databinding.ContactItemBinding
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.models.User

class UserAdapter(
    private val onUserItemClickListener: OnUserItemClickListener,
    private val showMultiselectDelete: (Boolean) -> Unit
    ) :
    ListAdapter<User, UserAdapter.ViewHolder>(UserItemDiffCallback()) {

    private var isMultiselectEnable = false
    private val itemSelectedList = mutableListOf<Int>()
    class UserItemDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }

    inner class ViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val contactNameView = binding.contactName
        val contactCareerView = binding.contactCareer
        val contactImageView = binding.userPhoto
        val contactDeleteIcon = binding.deleteIcon
        val contactSelectedIcon = binding.icUserSelected
        val contactUnselectedIcon = binding.icUserUnselected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {

        val screenHeight = holder.itemView.context.resources.displayMetrics.heightPixels
        val cardHeight = (screenHeight * Constants.CARD_HEIGHT_FRACTION).toInt()

        holder.itemView.layoutParams.height = cardHeight

        val user = getItem(position)

        holder.contactNameView.text = user.name
        holder.contactCareerView.text = user.career
        holder.contactImageView.loadImageByGlide(user.photo)

        if (isMultiselectEnable) {
            if (itemSelectedList.contains(position)) {
                holder.contactSelectedIcon.visibility = View.VISIBLE
                holder.contactUnselectedIcon.visibility = View.INVISIBLE
            } else {
                holder.contactSelectedIcon.visibility = View.INVISIBLE
                holder.contactUnselectedIcon.visibility = View.VISIBLE
            }
        }

        holder.itemView.setOnLongClickListener {
            selectItem(holder, user, position)

            notifyItemRangeChanged(0, this.itemCount)

            true
        }

        holder.itemView.setOnClickListener {
            if (isMultiselectEnable) {
                toggleItemSelection(position, holder, user)
            } else {
                onUserItemClickListener.onContactItemClicked(user)
            }
        }

        holder.contactDeleteIcon.setOnClickListener {
            onUserItemClickListener.onDeleteItemClicked(user, holder.adapterPosition)
        }
    }

    private fun toggleItemSelection(position: Int, holder: ViewHolder, user: User) {
        if (itemSelectedList.contains(position)) {

            itemSelectedList.removeAt(position)
            user.isSelected = false

            holder.contactSelectedIcon.visibility = View.INVISIBLE
            holder.contactUnselectedIcon.visibility = View.VISIBLE

            if (itemSelectedList.isEmpty()) {
                isMultiselectEnable = false
                showMultiselectDelete(false)
                notifyItemRangeChanged(0, this.itemCount)
            }
        } else {
            selectItem(holder, user, position)
        }
    }

    private fun selectItem(holder: ViewHolder, user: User, position: Int) {
        isMultiselectEnable = true
        itemSelectedList.add(position)

        user.isSelected = true
        showMultiselectDelete(true)
        holder.contactSelectedIcon.visibility = View.VISIBLE
        holder.contactUnselectedIcon.visibility = View.INVISIBLE
    }

    interface OnUserItemClickListener {
        fun onContactItemClicked(user: User)
        fun onDeleteItemClicked(user: User, position: Int)
    }
}