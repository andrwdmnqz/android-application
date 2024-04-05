package com.project.myproject.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.Constants
import com.project.myproject.R
import com.project.myproject.databinding.AddContactDialogBinding
import com.project.myproject.models.User
import com.project.myproject.viewmodels.UserViewModel

class AddContactDialogFragment(private val viewModel: UserViewModel) : DialogFragment() {

    private lateinit var viewBinding: AddContactDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = AddContactDialogBinding.inflate(layoutInflater)

        val newContactName = viewBinding.contactNameInput
        val newContactCareer = viewBinding.contactCareerInput
        val newContactNameLayout = viewBinding.contactNameLayout

        val dialog = AlertDialog.Builder(requireContext())
            .setView(viewBinding.root)
            .setMessage(getString(R.string.add_contact_message))
            .setPositiveButton(getString(R.string.add_contact_save)) { _, _ -> }
            .create()

        setupDialog(dialog, newContactName, newContactCareer, newContactNameLayout)

        setupNameValidation(viewBinding.contactNameLayout, viewBinding.contactNameInput)

        return dialog
    }

    private fun setupDialog(
        dialog: AlertDialog,
        newContactName: TextInputEditText,
        newContactCareer: TextInputEditText,
        newContactNameLayout: TextInputLayout
    ) {
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
    }

    private fun addSaveButtonListener(newContactName: TextInputEditText,
        newContactCareer: TextInputEditText, newContactNameLayout: TextInputLayout) {

        val name = newContactName.text.toString()
        val career = newContactCareer.text.toString()

        if (name.isNotBlank()) {
            val user = User(User.generateId(), Constants.DEFAULT_USER_IMAGE_PATH, name, career)
            viewModel.addUser(0, user)
            dismiss()
        } else {
            newContactNameLayout.error = getString(R.string.add_contact_validation)
        }
    }

    private fun setupNameValidation(addContactNameLayout: TextInputLayout, addContactNameInput: TextInputEditText) {
        addContactNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val name = s.toString()

                if (name.isNotEmpty()) {
                    addContactNameLayout.error = null
                }
            }
        })
    }

    companion object {
        const val TAG = "addContactFragment"
    }
}