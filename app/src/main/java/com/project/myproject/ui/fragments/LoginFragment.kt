package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentLoginBinding
import com.project.myproject.ui.fragments.watchers.EmailTextWatcher
import com.project.myproject.ui.fragments.watchers.PasswordTextWatcher
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
    private lateinit var loginEmailEditText: TextInputEditText
    private lateinit var loginPasswordLayout: TextInputLayout
    private lateinit var loginPasswordEditText: TextInputEditText

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setLoginCallbacks(this)

        loginEmailLayout = binding.tilLoginEmail
        loginPasswordLayout = binding.tilLoginPassword

        loginEmailEditText = binding.etLoginEmail
        loginPasswordEditText = binding.etLoginPassword

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

    private fun initializeSignUpViewListener() {
        val signUpView = binding.tvSignUpLabel

        signUpView.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun initializeLoginButtonListeners() {
        val registerButton = binding.btnLogin

        registerButton.setOnClickListener {
            val email = loginEmailEditText.text
            val password = loginPasswordEditText.text

            if (loginEmailLayout.error == null && loginPasswordLayout.error == null
                && !email.isNullOrBlank() && !password.isNullOrBlank()) {

                viewModel.loginUser(email.toString(), password.toString(), binding.chbRememberMeLogin.isChecked)
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
        loginPasswordEditText.addTextChangedListener(PasswordTextWatcher(loginPasswordLayout, requireContext()))
    }

    private fun setupEmailValidation() {
        loginEmailEditText.addTextChangedListener(EmailTextWatcher(loginEmailLayout, requireContext()))
    }

    override fun onSuccess() {
        findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
    }

    override fun onInvalidLoginData() {
        loginEmailLayout.error = getString(R.string.invalid_login_data)
        loginPasswordLayout.error = " "
    }
}