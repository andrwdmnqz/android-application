package com.project.myproject.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.models.User

class UserAdapter(var contactsList: ArrayList<User>) :
    RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    var recyclerView: RecyclerView? = null

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameView = itemView.findViewById<TextView>(R.id.contact_name)
        val contactCareerView = itemView.findViewById<TextView>(R.id.contact_career)
        val contactImageView = itemView.findViewById<ImageView>(R.id.user_photo)
        val contactDeleteIcon = itemView.findViewById<ImageView>(R.id.delete_icon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.contact_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: UserAdapter.ViewHolder, position: Int) {

        val screenHeight = holder.itemView.context.resources.displayMetrics.heightPixels
        // TODO export magic number to a constant
        val cardHeight = (screenHeight * 0.1).toInt()

        holder.itemView.layoutParams.height = cardHeight

        val user: User = contactsList[position]

        val contactNameField = holder.contactNameView
        val contactCareerField = holder.contactCareerView
        val contactImageField = holder.contactImageView
        val contactDeleteIcon = holder.contactDeleteIcon

        contactNameField.text = user.name
        contactCareerField.text = user.career
        contactImageField.loadImageByGlide(user.photo)
        contactDeleteIcon.setOnClickListener{ deleteItem(position, holder) }
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }

    fun deleteItem(index: Int, holder: ViewHolder) {
        val user = contactsList[index]

        contactsList.removeAt(index)
        notifyItemRemoved(index)

        val snackbar = Snackbar.make(holder.itemView,
            "contact has been removed", Snackbar.LENGTH_SHORT)

        snackbar.setAction("Undo") {
            contactsList.add(index, user)
            notifyItemInserted(index)
            recyclerView?.scrollToPosition(0)
            showToast("contact has been returned", holder)
        }
        snackbar.show()
    }

    fun addItem(name: String, career: String) {
        val user = User("https://i.pinimg.com/564x/61/f7/5e/61f75ea9a680def2ed1c6929fe75aeee.jpg", name, career)

        contactsList.add(0, user)
        notifyItemInserted(0)
        recyclerView?.scrollToPosition(0)
    }

    private fun showToast(message: String, holder: UserAdapter.ViewHolder) {
        val context = holder.itemView.context.applicationContext
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}