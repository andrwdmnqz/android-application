package com.project.myproject.ui.fragments.watchers

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R

class NameTextWatcher(private val nameLayout: TextInputLayout, private val context: Context) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(s: Editable?) {
        val nameAllowedSymbolsRegex = Regex(NAME_REGEX)
        val name = s.toString()
        when {
            name.isNotEmpty() && name.length < MINIMUM_NAME_LENGTH -> {
                nameLayout.error = context.getString(R.string.error_name_min_length)
            }

            name.isNotEmpty() && name.length > MAXIMUM_NAME_LENGTH -> {
                nameLayout.error = context.getString(R.string.error_name_max_length)
            }

            name.isNotEmpty() && !nameAllowedSymbolsRegex.matches(name) -> {
                nameLayout.error = context.getString(R.string.error_name_symbols)
            }

            else -> {
                nameLayout.error = null
            }
        }
    }

    companion object {
        const val MINIMUM_NAME_LENGTH = 3
        const val MAXIMUM_NAME_LENGTH = 32
        const val NAME_REGEX = "^[\\p{L}\\p{M}'\\- .]*\$"
    }
}