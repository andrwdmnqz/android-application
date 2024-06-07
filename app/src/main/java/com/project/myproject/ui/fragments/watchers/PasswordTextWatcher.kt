package com.project.myproject.ui.fragments.watchers

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.utils.Constants

class PasswordTextWatcher(private val passwordLayout: TextInputLayout, private val context: Context) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(s: Editable?) {
        val passwordAllowedSymbolsRegex = Regex(Constants.PASSWORD_REGEX)
        val password = s.toString()
        when {
            password.isNotEmpty() && password.length < Constants.MINIMUM_PASSWORD_LENGTH -> {
                passwordLayout.error = context.getString(R.string.error_password_min_length)
            }

            password.isNotEmpty() && password.length > Constants.MAXIMUM_PASSWORD_LENGTH -> {
                passwordLayout.error = context.getString(R.string.error_password_max_length)
            }

            password.isNotEmpty() && !passwordAllowedSymbolsRegex.matches(password) -> {
                passwordLayout.error = context.getString(R.string.error_password_symbols)
            }

            else -> {
                passwordLayout.error = null
            }
        }
    }
}