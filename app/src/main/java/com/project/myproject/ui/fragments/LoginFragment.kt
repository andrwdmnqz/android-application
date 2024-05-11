package com.project.myproject.ui.fragments

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentLoginBinding
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.LoginCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate),
    LoginCallbacks {

    private lateinit var loginEmailLayout: TextInputLayout
    private lateinit var loginEmailInput: TextInputEditText
    private lateinit var loginPasswordLayout: TextInputLayout
    private lateinit var loginPasswordInput: TextInputEditText

    @Inject
    lateinit var settingPreference: SettingPreference
    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setLoginCallbacks(this)

        loginEmailLayout = binding.tilLoginEmail
        loginPasswordLayout = binding.tilLoginPassword

        loginEmailInput = binding.tietLoginEmail
        loginPasswordInput = binding.tietLoginPassword

        setListeners()
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun setObservers() {
        setupEmailValidation()
        setupPasswordValidation()
    }

    override fun setListeners() {
        initializeLoginButtonListeners()
        initializeSignUpViewListener()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    private fun initializeSignUpViewListener() {
        val signUpView = binding.tvSignUp

        signUpView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun initializeLoginButtonListeners() {
        val registerButton = binding.buttonLogin

        registerButton.setOnClickListener {
            val email = loginEmailInput.text
            val password = loginPasswordInput.text

            if (loginEmailLayout.error == null && loginPasswordLayout.error == null
                && !email.isNullOrBlank() && !password.isNullOrBlank()) {

                viewModel.loginUser(email.toString(), password.toString())
            }

            if (email.isNullOrBlank()) {
                loginEmailLayout.error = getString(R.string.error_email_required)
            }

            if (password.isNullOrBlank()) {
                loginPasswordLayout.error = getString(R.string.error_password_required)
            }
        }
    }

    private fun setupPasswordValidation() {
        loginPasswordInput.addTextChangedListener(object : TextWatcher {
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
                        loginPasswordLayout.error = getString(R.string.error_password_min_length)
                    }

                    password.isNotEmpty() && password.length > Constants.MAXIMUM_PASSWORD_LENGTH -> {
                        loginPasswordLayout.error = getString(R.string.error_password_max_length)
                    }

                    password.isNotEmpty() && !passwordAllowedSymbolsRegex.matches(password) -> {
                        loginPasswordLayout.error = getString(R.string.error_password_symbols)
                    }

                    else -> {
                        loginPasswordLayout.error = null
                    }
                }
            }
        })
    }

    private fun setupEmailValidation() {
        loginEmailInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val email = s.toString()

                if (email.isNotEmpty() && !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    loginEmailLayout.error = getString(R.string.error_invalid_email)
                } else {
                    loginEmailLayout.error = null
                }
            }
        })
    }

    override fun onSuccess(accessToken: String, refreshToken: String, userId: Int) {
        val rememberMeCheckbox = binding.checkboxRememberMeLogin

        if (rememberMeCheckbox.isChecked) {
            lifecycleScope.launch {
                Log.d("DEBUG", "saving access token $accessToken")
                Log.d("DEBUG", "saving refresh token $refreshToken")
                Log.d("DEBUG", "saving id $userId")
                settingPreference.saveAccessToken(accessToken)
                settingPreference.saveRefreshToken(refreshToken)
                settingPreference.saveUserId(userId)
            }
        }

        sessionManager.setId(userId)
        sessionManager.setAccessToken(accessToken)
        sessionManager.setRefreshToken(refreshToken)
        sessionManager.setUserRememberState(rememberMeCheckbox.isChecked)

        findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
    }

    override fun onInvalidLoginData() {
        loginEmailLayout.error = getString(R.string.invalid_login_data)
        loginPasswordLayout.error = " "
    }
}