package com.project.myproject.ui.fragments.utils

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R

class PhoneTextWatcher(
    private val phoneLayout: TextInputLayout,
    private val phoneEditText: TextInputEditText,
    private val context: Context
) :
    TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        // Not used
    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        // Not used
    }

    override fun afterTextChanged(s: Editable?) {
        val phone = s.toString().replace("\\D".toRegex(), "")

        when {
            phone.isNotEmpty() && !Regex(MINIMUM_PHONE_LENGTH_REGEX).matches(phone) -> {
                phoneLayout.error = context.getString(R.string.error_phone_min_length)
            }

            phone.isNotEmpty() && !Regex(MAXIMUM_PHONE_LENGTH_REGEX).matches(phone) -> {
                phoneLayout.error = context.getString(R.string.error_phone_max_length)
            }

            else -> {
                phoneLayout.error = null
            }
        }
        formatPhoneNumber(phone)
    }

    private fun formatPhoneNumber(phone: String) {
        val formattedPhoneNumber = StringBuilder()

        for (i in phone.indices) {
            when (i) {
                0 -> formattedPhoneNumber.append("(")
                3 -> formattedPhoneNumber.append(") - ")
                6 -> formattedPhoneNumber.append(" - ")
                10 -> formattedPhoneNumber.append(" - ")
                else -> {
                }
            }
            formattedPhoneNumber.append(phone[i])
        }

        phoneEditText.removeTextChangedListener(this)
        phoneEditText.setText(formattedPhoneNumber.toString())
        phoneEditText.setSelection(formattedPhoneNumber.length)
        phoneEditText.addTextChangedListener(this)
    }

    companion object {
        const val MINIMUM_PHONE_LENGTH_REGEX = "^\\d{10,}\$"
        const val MAXIMUM_PHONE_LENGTH_REGEX = "^\\d{1,15}\$"
    }
}