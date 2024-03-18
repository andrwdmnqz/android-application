package com.project.myproject.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.R
import com.project.myproject.models.Contact

class ContactsAdapter(private val contactsList: List<Contact>) :
    RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val contactNameView = itemView.findViewById<TextView>(R.id.contact_name)
        val contactCareerView = itemView.findViewById<TextView>(R.id.contact_career)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        val contactView = inflater.inflate(R.layout.contact_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ContactsAdapter.ViewHolder, position: Int) {
        val contact: Contact = contactsList[position]

        val contactNameField = holder.contactNameView
        val contactCareerField = holder.contactCareerView

        contactNameField.text = contact.name
        contactCareerField.text = contact.career
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}