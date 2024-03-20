package com.project.myproject

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.databinding.RegisterActivityBinding
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var settingPreference: SettingPreference
    private lateinit var viewBinding: RegisterActivityBinding

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        viewBinding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val regEmailLayout = viewBinding.regEmailLayout
        val regEmailInput = viewBinding.regEmailInput

        val regPasswordLayout = viewBinding.regPasswordLayout
        val regPasswordInput = viewBinding.regPasswordInput

        settingPreference = SettingPreference(this)

        initializeRegisterButton(regEmailLayout, regPasswordLayout, regEmailInput, regPasswordInput)

        emailValidation(regEmailInput, regEmailLayout)

        passwordValidation(regPasswordInput, regPasswordLayout)
    }

    private fun passwordValidation(regPasswordInput: TextInputEditText, regPasswordLayout: TextInputLayout) {
        regPasswordInput.addTextChangedListener(object : TextWatcher {
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
                    password.isNotEmpty() && password.length < 8 -> {
                        regPasswordLayout.error =
                            "Your password must include a minimum of 8 characters."
                    }

                    password.isNotEmpty() && password.length > 16 -> {
                        regPasswordLayout.error =
                            "Your password must include a maximum of 16 characters."
                    }

                    password.isNotEmpty() && !passwordAllowedSymbolsRegex.matches(password) -> {
                        regPasswordLayout.error =
                            "Your password must include only letters, numbers and symbols."
                    }

                    else -> {
                        regPasswordLayout.error = null
                    }
                }
            }
        })
    }

    private fun emailValidation(regEmailInput: TextInputEditText, regEmailLayout: TextInputLayout) {
        regEmailInput.addTextChangedListener(object : TextWatcher {
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
    }

    private fun initializeRegisterButton(regEmailLayout: TextInputLayout,
        regPasswordLayout: TextInputLayout, regEmailInput: TextInputEditText,
                                         regPasswordInput: TextInputEditText) {

        val registerButton = viewBinding.registerButton
        val rememberMeCheckbox = viewBinding.rememberMe

        registerButton.setOnClickListener {
            if (regEmailLayout.error == null && regPasswordLayout.error == null
                && !regEmailInput.text.isNullOrBlank() && !regPasswordInput.text.isNullOrBlank()) {

                lifecycleScope.launch {
                    val registerIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                    registerIntent.putExtra(Constants.EMAIL_KEY, regEmailInput.text.toString())
                    val profileActivityOptions =
                        ActivityOptions.makeSceneTransitionAnimation(this@RegisterActivity)

                    if (rememberMeCheckbox.isChecked) {
                        settingPreference.saveEmail(regEmailInput.text.toString())
                        settingPreference.savePassword(regPasswordInput.text.toString())
                    }

                    startActivity(registerIntent, profileActivityOptions.toBundle())
                    finish()
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch {
            val email = settingPreference.getEmail().firstOrNull()
            val password = settingPreference.getPassword().firstOrNull()

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                val registerIntent = Intent(this@RegisterActivity, MainActivity::class.java)
                registerIntent.putExtra(Constants.EMAIL_KEY, email)
                val profileActivityOptions =
                    ActivityOptions.makeSceneTransitionAnimation(this@RegisterActivity)

                startActivity(registerIntent, profileActivityOptions.toBundle())
                finish()
            }
        }
    }
}