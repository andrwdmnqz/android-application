package com.project.myproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.R
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.models.User

class UserAdapter(var contactsList: List<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameView = itemView.findViewById<TextView>(R.id.contact_name)
        val contactCareerView = itemView.findViewById<TextView>(R.id.contact_career)
        val contactImageView = itemView.findViewById<ImageView>(R.id.user_photo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.contact_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {
        val user: User = contactsList[position]

        val contactNameField = holder.contactNameView
        val contactCareerField = holder.contactCareerView
        val contactImageField = holder.contactImageView

        contactNameField.text = user.name
        contactCareerField.text = user.career
        contactImageField.loadImageByGlide(user.photo)
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}