package com.project.myproject.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.Constants
import com.project.myproject.databinding.ContactItemBinding
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.models.User

class UserAdapter(private val onDeleteItemClickListener: OnDeleteItemClickListener,
                  var contactsList: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ContactItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val user: User
            get() = contactsList[adapterPosition]

        val contactNameView = binding.contactName
        val contactCareerView = binding.contactCareer
        val contactImageView = binding.userPhoto
        val contactDeleteIcon = binding.deleteIcon
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val binding = ContactItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {

        val screenHeight = holder.itemView.context.resources.displayMetrics.heightPixels
        val cardHeight = (screenHeight * Constants.CARD_HEIGHT_FRACTION).toInt()

        holder.itemView.layoutParams.height = cardHeight

        val user: User = contactsList[position]

        holder.contactNameView.text = user.name
        holder.contactCareerView.text = user.career
        holder.contactImageView.loadImageByGlide(user.photo)
        holder.contactDeleteIcon.setOnClickListener{
            onDeleteItemClickListener.onDeleteItemClicked(user, position)
        }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    interface OnDeleteItemClickListener {
        fun onDeleteItemClicked(user: User, position: Int)
    }
}