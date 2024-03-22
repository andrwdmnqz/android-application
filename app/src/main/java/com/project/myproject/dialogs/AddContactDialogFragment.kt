package com.project.myproject.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.databinding.AddContactDialogBinding

class AddContactDialogFragment(private val adapter: UserAdapter) : DialogFragment() {

    private lateinit var viewBinding: AddContactDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = AddContactDialogBinding.inflate(layoutInflater)

        val newContactName = viewBinding.contactName
        val newContactCareer = viewBinding.contactCareer

        return AlertDialog.Builder(requireContext())
            .setView(viewBinding.root)
            .setMessage("Add contact")
            .setPositiveButton("Save") { _, _ ->
                val name = newContactName.text.toString()
                val career = newContactCareer.text.toString()

                if (name.isNotBlank() && career.isNotBlank()) {
                    adapter.addItem(newContactName.text.toString(), newContactCareer.text.toString())
                }

            }
            .create()
    }

    companion object {
        const val TAG = "addContactFragment"
    }
}