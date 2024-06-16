package com.project.myproject.ui.fragments.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R

class EmailTextWatcher(private val emailLayout: TextInputLayout, private val context: Context) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(s: Editable?) {
        val email = s.toString()

        if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.error = context.getString(R.string.error_invalid_email)
        } else {
            emailLayout.error = null
        }
    }
}