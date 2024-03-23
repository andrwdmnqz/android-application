package com.project.myproject.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.databinding.AddContactDialogBinding

class AddContactDialogFragment(private val adapter: UserAdapter) : DialogFragment() {

    private lateinit var viewBinding: AddContactDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = AddContactDialogBinding.inflate(layoutInflater)

        val newContactName = viewBinding.contactNameInput
        val newContactCareer = viewBinding.contactCareerInput
        val newContactNameLayout = viewBinding.contactNameLayout

        val dialog = AlertDialog.Builder(requireContext())
            .setView(viewBinding.root)
            .setMessage("Add contact")
            .setPositiveButton("Save") { _, _ -> }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener {
                addSaveButtonListener(
                    newContactName,
                    newContactCareer,
                    newContactNameLayout
                )
            }

            dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.additional_text_first
                )
            )
            dialog.window?.setBackgroundDrawable(
                ColorDrawable(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.default_background
                    )
                )
            )
        }

        return dialog
    }

    private fun addSaveButtonListener(newContactName: TextInputEditText,
        newContactCareer: TextInputEditText, newContactNameLayout: TextInputLayout) {

        val name = newContactName.text.toString()
        val career = newContactCareer.text.toString()

        if (name.isNotBlank()) {
            adapter.addItem(name, career)
            dismiss()
        } else {
            newContactNameLayout.error = "Name shouldn't be empty!"
        }
    }

    companion object {
        const val TAG = "addContactFragment"
    }
}