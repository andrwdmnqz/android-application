package com.project.myproject.ui.adapters

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.databinding.ContactItemBinding
import com.project.myproject.utils.extensions.loadImageByGlide
import com.project.myproject.data.models.User
import com.project.myproject.utils.callbacks.UserItemDiffCallback

class UserAdapter(
    context: Context,
    private val onUserItemClickListener: OnUserItemClickListener,
    private val showMultiselectDelete: (Boolean) -> Unit
    ) :
    ListAdapter<User, UserAdapter.ViewHolder>(UserItemDiffCallback()) {
    private var isMultiselectEnable = false
    private val itemSelectedList = mutableListOf<Int>()

    private val shiftAmount = context.resources.getDimensionPixelSize(R.dimen.multiselect_view_shift_right).toFloat()

    inner class ViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val contactNameView = binding.tvContactName
        val contactCareerView = binding.tvContactCareer
        val contactImageView = binding.ivContactPhoto
        val contactDeleteIcon = binding.ivContactDeleteIcon
        val contactSelectedIcon = binding.icUserSelected
        val contactUnselectedIcon = binding.icUserUnselected
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val screenHeight = holder.itemView.context.resources.displayMetrics.heightPixels
        val cardHeight = (screenHeight * Constants.CARD_HEIGHT_FRACTION).toInt()

        holder.itemView.layoutParams.height = cardHeight

        val user = getItem(position)

        with (holder) {
            contactNameView.text = user.name
            contactCareerView.text = user.career
            contactImageView.loadImageByGlide(user.photo)
        }

        defineItemViewsAppearance(holder, position)

        shiftViews(holder, isMultiselectEnable)

        setItemListeners(holder, user)
    }

    private fun setItemListeners(holder: ViewHolder, user: User) {
        holder.itemView.setOnLongClickListener {

            selectItem(holder, user, holder.adapterPosition)
            notifyItemRangeChanged(0, this.itemCount)

            true
        }

        holder.itemView.setOnClickListener {
            if (isMultiselectEnable) {
                toggleItemSelection(holder.adapterPosition, holder, user)
            } else {
                onUserItemClickListener.onContactItemClicked(user)
            }
        }

        holder.contactDeleteIcon.setOnClickListener {
            onUserItemClickListener.onDeleteItemClicked(user, holder.adapterPosition)
        }
    }

    private fun shiftViews(holder: ViewHolder, shift: Boolean) {
        with (holder) {
            contactImageView.translationX = if (shift) shiftAmount else 0f
            contactNameView.translationX = if (shift) shiftAmount else 0f
            contactCareerView.translationX = if (shift) shiftAmount else 0f
        }
    }

    private fun defineItemViewsAppearance(holder: ViewHolder, position: Int) {

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
            deleteIcon.visibility = View.INVISIBLE
            markIsSelected(position, selectedIcon, unselectedIcon)
            backgroundColor = ContextCompat.getColor(holder.itemView.context, R.color.selected_background)
        }
        shapeDrawable.setColor(backgroundColor)
    }

    private fun markIsSelected(position: Int, selectedIcon: View, unselectedIcon: View) {
        if (itemSelectedList.contains(position)) {
            selectedIcon.visibility = View.VISIBLE
            unselectedIcon.visibility = View.INVISIBLE
        } else {
            selectedIcon.visibility = View.INVISIBLE
            unselectedIcon.visibility = View.VISIBLE
        }
    }

    private fun toggleItemSelection(position: Int, holder: ViewHolder, user: User) {
        if (itemSelectedList.contains(position)) {

            itemSelectedList.remove(position)
            user.isSelected = false

            changeVisibility(holder)

            if (itemSelectedList.isEmpty()) {
                exitMultiselectMode()
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

        changeVisibility(holder)
    }

    private fun changeVisibility(holder: ViewHolder) {
        val selectedIcon = holder.contactSelectedIcon
        val unselectedIcon = holder.contactUnselectedIcon

        val tempVisibility = selectedIcon.visibility
        selectedIcon.visibility = unselectedIcon.visibility
        unselectedIcon.visibility = tempVisibility
    }

    fun exitMultiselectMode() {
        isMultiselectEnable = false
        showMultiselectDelete(false)

        itemSelectedList.clear()
        notifyItemRangeChanged(0, itemCount)
    }

    interface OnUserItemClickListener {
        fun onContactItemClicked(user: User)
        fun onDeleteItemClicked(user: User, position: Int)
    }

    fun getSelectedItems() = itemSelectedList
}