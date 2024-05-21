package com.project.myproject.ui.dialogs

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.databinding.AddContactDialogBinding

class AddContactDialogFragment : DialogFragment() {

    private lateinit var viewBinding: AddContactDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        viewBinding = AddContactDialogBinding.inflate(layoutInflater)

        val newContactName = viewBinding.etAddContactName
        val newContactCareer = viewBinding.etAddContactCareer
        val newContactAddress = viewBinding.etAddContactAddress
        val newContactNameLayout = viewBinding.tilAddContactName

        val dialog = AlertDialog.Builder(requireContext())
            .setView(viewBinding.root)
            .setMessage(getString(R.string.add_contact_message))
            .setPositiveButton(getString(R.string.add_contact_save)) { _, _ -> }
            .create()

        setupDialog(dialog, newContactName, newContactCareer, newContactAddress, newContactNameLayout)

        setupNameValidation(viewBinding.tilAddContactName, viewBinding.etAddContactName)

        return dialog
    }

    private fun setupDialog(
        dialog: AlertDialog,
        newContactName: TextInputEditText,
        newContactCareer: TextInputEditText,
        newContactAddress: TextInputEditText,
        newContactNameLayout: TextInputLayout
    ) {
        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener {
                addSaveButtonListener(
                    newContactName,
                    newContactCareer,
                    newContactAddress,
                    newContactNameLayout
                )
            }

            dialog.findViewById<TextView>(android.R.id.message)?.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.additional_fifth
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
        newContactCareer: TextInputEditText, newContactAddress: TextInputEditText,
            newContactNameLayout: TextInputLayout) {

        val name = newContactName.text.toString()
        val career = newContactCareer.text.toString()
        val address = newContactAddress.text.toString()

        if (name.isNotBlank()) {
            val bundle = Bundle().apply {
                putString(Constants.CONTACT_NAME_KEY, name)
                putString(Constants.CONTACT_CAREER_KEY, career)
                putString(Constants.CONTACT_ADDRESS_KEY, address)
            }
            setFragmentResult(Constants.CONTACT_INFO_KEY, bundle)
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
