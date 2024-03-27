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

private const val PASSWORD_REGEX = "^[a-zA-Z0-9@#\$%^&+=!]+\$"

class RegisterActivity : AppCompatActivity() {

    private lateinit var settingPreference: SettingPreference
    private lateinit var viewBinding: RegisterActivityBinding

    private lateinit var regEmailInput: TextInputEditText
    private lateinit var regPasswordInput: TextInputEditText

    override fun onCreate(savedInitialState: Bundle?) {
        super.onCreate(savedInitialState)
        viewBinding = RegisterActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val regEmailLayout = viewBinding.regEmailLayout
        val regPasswordLayout = viewBinding.regPasswordLayout

        regEmailInput = viewBinding.regEmailInput
        regPasswordInput = viewBinding.regPasswordInput

        settingPreference = SettingPreference(this)

        initializeRegisterButton(regEmailLayout, regPasswordLayout)

        emailValidation(regEmailLayout)

        passwordValidation(regPasswordLayout)
    }

    private fun passwordValidation(regPasswordLayout: TextInputLayout) {
        regPasswordInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val passwordAllowedSymbolsRegex = Regex(PASSWORD_REGEX)
                val password = s.toString()
                when {
                    password.isNotEmpty() && password.length < 8 -> {
                        regPasswordLayout.error = getString(R.string.error_password_min_length)
                    }

                    password.isNotEmpty() && password.length > 16 -> {
                        regPasswordLayout.error = getString(R.string.error_password_max_length)
                    }

                    password.isNotEmpty() && !passwordAllowedSymbolsRegex.matches(password) -> {
                        regPasswordLayout.error = getString(R.string.error_password_symbols)
                    }

                    else -> {
                        regPasswordLayout.error = null
                    }
                }
            }
        })
    }

    private fun emailValidation(regEmailLayout: TextInputLayout) {
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
                    regEmailLayout.error = getString(R.string.error_invalid_email)
                } else {
                    regEmailLayout.error = null
                }
            }
        })
    }

    private fun initializeRegisterButton(regEmailLayout: TextInputLayout,
        regPasswordLayout: TextInputLayout) {

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

            if (regEmailInput.text.isNullOrBlank()) {
                regEmailLayout.error = getString(R.string.error_email_required)
            }

            if (regPasswordInput.text.isNullOrBlank()) {
                regPasswordLayout.error = getString(R.string.error_password_required)
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

    override fun onSaveInstanceState(outState: Bundle) {

        outState.putString(Constants.EMAIL_KEY, regEmailInput.text.toString())
        outState.putString(Constants.PASSWORD_KEY, regPasswordInput.text.toString())
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        regEmailInput.setText(savedInstanceState.getString(Constants.EMAIL_KEY))
        regPasswordInput.setText(savedInstanceState.getString(Constants.PASSWORD_KEY))
    }
}