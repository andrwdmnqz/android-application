package com.project.myproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.transition.Transition
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentRegisterBinding
import com.project.myproject.ui.viewmodels.TokenCallbacks
import com.project.myproject.ui.viewmodels.RegistrationCallbacks
import com.project.myproject.ui.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate),
    RegistrationCallbacks,
    TokenCallbacks {

    private lateinit var regEmailLayout: TextInputLayout
    private lateinit var regEmailInput: TextInputEditText
    private lateinit var regPasswordInput: TextInputEditText

    private lateinit var settingPreference: SettingPreference

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        settingPreference = SettingPreference(requireContext())
        viewModel.setRegistrationCallbacks(this)
        viewModel.setOverallCallbacks(this)

        regEmailLayout = binding.regEmailLayout
        val regPasswordLayout = binding.regPasswordLayout

        regEmailInput = binding.regEmailInput
        regPasswordInput = binding.regPasswordInput

        initializeRegisterButtonListeners(regPasswordLayout)
        initializeSignInViewListener()
        setupEmailValidation()
        setupPasswordValidation(regPasswordLayout)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeSignInViewListener() {
        val signInView = binding.tvSignIn

        signInView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupPasswordValidation(regPasswordLayout: TextInputLayout) {
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

    private fun setupEmailValidation() {
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

    private fun initializeRegisterButtonListeners(
        regPasswordLayout: TextInputLayout
    ) {
        val registerButton = binding.registerButton

        registerButton.setOnClickListener {
            val email = regEmailInput.text
            val password = regPasswordInput.text

            if (regEmailLayout.error == null && regPasswordLayout.error == null
                && !email.isNullOrBlank() && !password.isNullOrBlank()) {

                viewModel.registerUser(email.toString(), password.toString())
            }

            if (email.isNullOrBlank()) {
                regEmailLayout.error = getString(R.string.error_email_required)
            }

            if (password.isNullOrBlank()) {
                regPasswordLayout.error = getString(R.string.error_password_required)
            }
        }
    }

    private fun hideAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            view.visibility = View.INVISIBLE
        }
        binding.registerBackground.visibility = View.VISIBLE
    }

    private fun fadeAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            if (view == binding.registerBackground) {
                continue
            }

            view.visibility = View.VISIBLE

            val fadeInAnimation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
            view.startAnimation(fadeInAnimation)
        }
    }

    override fun onStart() {
        super.onStart()
        hideAllViewsExceptBackground()

        Handler().postDelayed({
            fadeAllViewsExceptBackground()
        }, 1000)

        lifecycleScope.launch {

            val accessToken = settingPreference.getAccessToken().firstOrNull()
            val refreshToken = settingPreference.getRefreshToken().firstOrNull()
            val userId = settingPreference.getUserId().firstOrNull()
            Log.d("DEBUG", "refresh token - $refreshToken")

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && userId != null) {
                viewModel.getUser(userId, accessToken)
            }
        }
    }

    override fun setObservers() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    override fun onEmailTakenError() {
        regEmailLayout.error = getString(R.string.error_email_exists)
    }

    override fun onSuccess(accessToken: String, refreshToken: String, userId: Int) {
        val rememberMeCheckbox = binding.rememberMe

        if (rememberMeCheckbox.isChecked) {
            lifecycleScope.launch {
                settingPreference.saveAccessToken(accessToken)
                settingPreference.saveRefreshToken(refreshToken)
                settingPreference.saveUserId(userId)
            }
            Log.d("DEBUG", "Saved id - $userId, saved access - $accessToken, saved refresh - $refreshToken")
        }

        findNavController().navigate(R.id.action_registerFragment_to_profileDataFragment)
    }

    override fun onUserIsRemembered() {
        findNavController().navigate(R.id.viewPagerFragment)
    }

    override fun onTokensRefreshFailure() {
        findNavController().navigate(R.id.registerFragment)
    }
}