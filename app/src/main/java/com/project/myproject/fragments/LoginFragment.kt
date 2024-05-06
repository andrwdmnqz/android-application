package com.project.myproject.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.Constants
import com.project.myproject.R
import com.project.myproject.SettingPreference
import com.project.myproject.databinding.FragmentLoginBinding
import com.project.myproject.viewmodels.LoginCallbacks
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class LoginFragment : Fragment(R.layout.fragment_login), LoginCallbacks {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private lateinit var loginEmailLayout: TextInputLayout
    private lateinit var loginEmailInput: TextInputEditText
    private lateinit var loginPasswordLayout: TextInputLayout
    private lateinit var loginPasswordInput: TextInputEditText

    private lateinit var settingPreference: SettingPreference

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)

        settingPreference = SettingPreference(requireContext())
        viewModel.setLoginCallbacks(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        loginEmailLayout = binding.tilLoginEmail
        loginPasswordLayout = binding.tilLoginPassword

        loginEmailInput = binding.tietLoginEmail
        loginPasswordInput = binding.tietLoginPassword

        initializeLoginButtonListeners()
        initializeSignUpViewListener()
        setupEmailValidation()
        setupPasswordValidation()

        super.onViewCreated(view, savedInstanceState)
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
                    password.isNotEmpty() && password.length < 8 -> {
                        loginPasswordLayout.error = getString(R.string.error_password_min_length)
                    }

                    password.isNotEmpty() && password.length > 16 -> {
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
        val rememberMeCheckbox = binding.chbRememberMeLogin

        if (rememberMeCheckbox.isChecked) {
            lifecycleScope.launch {
                settingPreference.saveAccessToken(accessToken)
                settingPreference.saveRefreshToken(refreshToken)
                settingPreference.saveUserId(userId)
            }
        }

        findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
    }

    override fun onInvalidLoginData() {
        loginEmailLayout.error = getString(R.string.invalid_login_data)
        loginPasswordLayout.error = " "
    }
}