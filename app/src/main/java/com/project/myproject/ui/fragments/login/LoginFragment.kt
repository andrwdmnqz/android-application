package com.project.myproject.ui.fragments.login

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.myproject.R
import com.project.myproject.databinding.FragmentLoginBinding
import com.project.myproject.ui.fragments.base.BaseFragment
import com.project.myproject.ui.fragments.login.viewmodel.LoginViewModel
import com.project.myproject.ui.fragments.viewmodel.LoginState
import com.project.myproject.ui.fragments.viewmodel.SharedViewModel
import com.project.myproject.ui.fragments.utils.EmailTextWatcher
import com.project.myproject.ui.fragments.utils.PasswordTextWatcher
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(FragmentLoginBinding::inflate) {

    private val viewModel: LoginViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        setListeners()
        setObservers()
    }

    private fun initializeUI() {
        with(binding) {
            tilLoginEmail.editText?.addTextChangedListener(EmailTextWatcher(tilLoginEmail, requireContext()))
            tilLoginPassword.editText?.addTextChangedListener(PasswordTextWatcher(tilLoginPassword, requireContext()))
        }
    }

    override fun setListeners() {
        binding.btnLogin.setOnClickListener {
            attemptLogin()
        }

        binding.tvSignUpLabel.setOnClickListener {
            navigateToSignUp()
        }
    }

    private fun attemptLogin() {
        val email = binding.etLoginEmail.text.toString()
        val password = binding.etLoginPassword.text.toString()
        val rememberMe = binding.chbRememberMeLogin.isChecked

        if (isValidInput(email, password)) {
            viewModel.loginUser(email, password, rememberMe)
        } else {
            showInputErrors(email, password)
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return binding.tilLoginEmail.error == null && binding.tilLoginPassword.error == null &&
                email.isNotBlank() && password.isNotBlank()
    }

    private fun showInputErrors(email: String, password: String) {
        if (email.isBlank()) {
            binding.tilLoginEmail.error = getString(R.string.error_email_required)
        }

        if (password.isBlank()) {
            binding.tilLoginPassword.error = getString(R.string.error_password_required)
        }
    }

    private fun navigateToSignUp() {
        findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
    }

    override fun setObservers() {
        observeLoginState()
    }

    private fun observeLoginState() {
        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Success -> navigateToViewPager()
                    is LoginState.InvalidLoginData -> onInvalidLoginData()
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                sharedViewModel.setLoginState(state)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                sharedViewModel.setCurrentUser(user)
            }
        }
    }

    private fun navigateToViewPager() {
        findNavController().navigate(R.id.action_loginFragment_to_viewPagerFragment)
    }

    private fun onInvalidLoginData() {
        binding.tilLoginEmail.error = getString(R.string.invalid_login_data)
        binding.tilLoginPassword.error = " "
    }
}