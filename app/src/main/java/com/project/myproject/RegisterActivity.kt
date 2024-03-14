package com.project.myproject

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var settingPreference: SettingPreference
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        setContentView(R.layout.register_activity)

        val registerButton = findViewById<Button>(R.id.register_button)

        val regEmailLayout = findViewById<TextInputLayout>(R.id.reg_email_layout)
        val regEmailInput = findViewById<TextInputEditText>(R.id.reg_email_input)

        val regPasswordLayout = findViewById<TextInputLayout>(R.id.reg_password_layout)
        val regPasswordInput = findViewById<TextInputEditText>(R.id.reg_password_input)

        settingPreference = SettingPreference(this)

        registerButton.setOnClickListener {
            if (regEmailLayout.error == null && regPasswordLayout.error == null
                && !regEmailInput.text.isNullOrBlank() && !regPasswordInput.text.isNullOrBlank()) {

                lifecycleScope.launch {
                    val registerIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                    registerIntent.putExtra("email", regEmailInput.text.toString())
                    val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@RegisterActivity)

                    settingPreference.saveEmail(regEmailInput.text.toString())
                    settingPreference.savePassword(regPasswordInput.text.toString())

                    startActivity(registerIntent, profileActivityOptions.toBundle())
                    finish()
                }
            }
        }

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
                val passwordAllowedSymbolsRegex = Regex("^[a-zA-Z0-9@#\$%^&+=!]+\$")
                val password = s.toString()
                when {
                    password.isNotEmpty() && password.length < 8 -> {
                        regPasswordLayout.error = "Your password must include a minimum of 8 characters."
                    }
                    password.isNotEmpty() && password.length > 16 -> {
                        regPasswordLayout.error = "Your password must include a maximum of 16 characters."
                    }
                    password.isNotEmpty() && !passwordAllowedSymbolsRegex.matches(password) -> {
                        regPasswordLayout.error = "Your password must include only letters, numbers and symbols."
                    }
                    else -> {
                        regPasswordLayout.error = null
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val email = settingPreference.getEmail().firstOrNull()
            val password = settingPreference.getPassword().firstOrNull()

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                val registerIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                registerIntent.putExtra("email", email)
                val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this@RegisterActivity)

                startActivity(registerIntent, profileActivityOptions.toBundle())
                finish()
            }
        }
    }
}