package com.project.myproject.adapters

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.Constants
import com.project.myproject.R
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

        Log.d("DEBUG", "${this.currentList}")

        Log.d("DEBUG", "pre/ position - $position, user name - ${user.name}")
        defineItemViewsVisibility(holder, position)

        holder.itemView.setOnLongClickListener {
            Log.d("DEBUG", "long click position - $position, user name - ${user.name}")

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

    private fun defineItemViewsVisibility(holder: ViewHolder, position: Int) {
        Log.d("DEBUG", "name - ${holder.contactNameView.text}, position - $position")
        val deleteIcon = holder.contactDeleteIcon
        val selectedIcon = holder.contactSelectedIcon
        val unselectedIcon = holder.contactUnselectedIcon

        val constraintLayout = holder.itemView as ConstraintLayout
        val shapeDrawable = constraintLayout.background as GradientDrawable
        val backgroundColor: Int

        if (!isMultiselectEnable) {

            deleteIcon.visibility = View.VISIBLE
            selectedIcon.visibility = View.INVISIBLE
            unselectedIcon.visibility = View.INVISIBLE

            backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.default_background)
        } else {
            holder.contactDeleteIcon.visibility = View.INVISIBLE
            if (itemSelectedList.contains(position)) {
                holder.contactSelectedIcon.visibility = View.VISIBLE
                holder.contactUnselectedIcon.visibility = View.INVISIBLE
            } else {
                holder.contactSelectedIcon.visibility = View.INVISIBLE
                holder.contactUnselectedIcon.visibility = View.VISIBLE
            }
            backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.selected_background)
        }
        shapeDrawable.setColor(backgroundColor)
    }

    private fun toggleItemSelection(position: Int, holder: ViewHolder, user: User) {
        if (itemSelectedList.contains(position)) {

            itemSelectedList.remove(position)
            user.isSelected = false

            changeVisibility(holder)

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
        Log.d("DEBUG", "$itemSelectedList")
        Log.d("DEBUG", "selecting user ${user.name}, position - $position")
        user.isSelected = true
        showMultiselectDelete(true)

        changeVisibility(holder)
    }

    private fun changeVisibility(holder: ViewHolder) {
        val selectedIcon = holder.contactSelectedIcon
        val unselectedIcon = holder.contactUnselectedIcon
        val tempVisibility = selectedIcon.visibility

        selectedIcon.visibility = unselectedIcon.visibility
        unselectedIcon.visibility = tempVisibility
    }

    interface OnUserItemClickListener {
        fun onContactItemClicked(user: User)
        fun onDeleteItemClicked(user: User, position: Int)
    }
}