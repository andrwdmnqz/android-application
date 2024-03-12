package com.project.myproject

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private var email: String? = null
    private var password: String? = null

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        setContentView(R.layout.register_activity)

        val regEmailLayout = findViewById<TextInputLayout>(R.id.reg_email_layout)
        val regEmailInput = findViewById<TextInputEditText>(R.id.reg_email_input)

        val regPasswordLayout = findViewById<TextInputLayout>(R.id.reg_password_layout)
        val regPasswordInput = findViewById<TextInputEditText>(R.id.reg_password_input)

        val registerButton = findViewById<Button>(R.id.register_button)

        sharedPreferences = getSharedPreferences("shared_preferences", Context.MODE_PRIVATE)

        email = sharedPreferences.getString("email_key", null)
        password = sharedPreferences.getString("password_key", null)

        registerButton.setOnClickListener {
            if (regEmailLayout.error == null && regPasswordLayout.error == null) {

                val editor = sharedPreferences.edit()

                editor.putString("email_key", regEmailInput.text.toString())
                editor.putString("password_key", regPasswordInput.text.toString())

                editor.apply()

                val registerIntent = Intent(this, MainActivity::class.java)

                registerIntent.putExtra("email", regEmailInput.text.toString())

                val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this)
                startActivity(registerIntent, profileActivityOptions.toBundle())
                finish()
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
                val passwordRegex = Regex("^[a-zA-Z0-9@#\$%^&+=!]+\$")
                val password = s.toString()
                when {
                    password.isNotEmpty() && password.length < 8 -> {
                        regPasswordLayout.error = "Your password must include a minimum of 8 characters."
                    }
                    password.isNotEmpty() && password.length > 16 -> {
                        regPasswordLayout.error = "Your password must include a maximum of 16 characters."
                    }
                    password.isNotEmpty() && passwordRegex.matches(password) -> {
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
        if (email != null && password != null) {
            val registerIntent = Intent(this, MainActivity::class.java)

            val profileActivityOptions = ActivityOptions.makeSceneTransitionAnimation(this)
            startActivity(registerIntent, profileActivityOptions.toBundle())
            finish()
        }
    }
}