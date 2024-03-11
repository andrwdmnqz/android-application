package com.project.myproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        setContentView(R.layout.register_activity)

        val regEmailLayout = findViewById<TextInputLayout>(R.id.reg_email_layout)
        val regEmailInput = findViewById<TextInputEditText>(R.id.reg_email_input)

        val regPasswordLayout = findViewById<TextInputLayout>(R.id.reg_password_layout)
        val regPasswordInput = findViewById<TextInputEditText>(R.id.reg_password_input)

        regEmailInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()
                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    regEmailLayout.error = "Incorrect E-Mail address"
                } else {
                    regEmailLayout.error = null
                }
            }
        })

        regPasswordInput.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val passwordRegex = Regex("^[a-zA-Z0-9@#\$%^&+=!]+\$\n")
                val password = s.toString()
                when {
                    password.isNotEmpty() && password.length < 8 -> {
                        regPasswordLayout.error = "Your password must include a minimum of 8 characters."
                    }
                    password.isNotEmpty() && password.length > 16 -> {
                        regPasswordLayout.error = "Your password must include a maximum of 16 characters."
                    }
                    password.isNotEmpty() && passwordRegex.matches(password) -> {
                        regPasswordLayout.error = "Your should contain only letters, numbers and symbols."
                    }
                    else -> {
                        regPasswordLayout.error = null
                    }
                 }
            }
        })
    }
}