package com.project.myproject.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.R
import com.project.myproject.data.models.User
import com.project.myproject.databinding.UserItemBinding
import com.project.myproject.utils.Constants
import com.project.myproject.utils.callbacks.UserItemDiffCallback
import com.project.myproject.utils.extensions.loadImageByGlide

class UserAdapter(private val onUserItemClickListener: OnUserItemCLickListener) :
    ListAdapter<User, UserAdapter.ViewHolder>(UserItemDiffCallback()) {

    inner class ViewHolder(private val binding: UserItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val userNameView = binding.tvUserName
        val userCareerView = binding.tvUserCareer
        val userImageView = binding.ivUserPhoto
        val addContactIcon = binding.ivAddContactIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = UserItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val screenHeight = holder.itemView.context.resources.displayMetrics.heightPixels
        val cardHeight = (screenHeight * Constants.CARD_HEIGHT_FRACTION).toInt()

        holder.itemView.layoutParams.height = cardHeight

        val user = getItem(position)

        with(holder) {
            if (user.image != null) {
                userImageView.loadImageByGlide(user.image!!)
            } else {
                userImageView.setImageResource(R.mipmap.empty_photo_icon)
            }
            userNameView.text = user.name.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_NAME_VALUE
            userCareerView.text = user.career.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_CAREER_VALUE
        }

        setItemListeners(holder, user)
    }

    private fun setItemListeners(holder: ViewHolder, user: User) {
        holder.itemView.setOnClickListener {
            onUserItemClickListener.onUserItemClicked(user)
        }

        holder.addContactIcon.setOnClickListener {
            onUserItemClickListener.onAddItemClicked(user)
        }
    }

    interface OnUserItemCLickListener {
        fun onUserItemClicked(user: User)

        fun onAddItemClicked(user: User)
    }
}