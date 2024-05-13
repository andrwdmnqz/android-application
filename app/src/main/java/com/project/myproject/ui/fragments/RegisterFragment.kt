package com.project.myproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentRegisterBinding
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.RegistrationCallbacks
import com.project.myproject.utils.callbacks.TokenCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment :
    BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate),
    RegistrationCallbacks,
    TokenCallbacks {

    @Inject
    lateinit var settingPreference: SettingPreference
    @Inject
    lateinit var sessionManager: SessionManager

    private lateinit var regEmailLayout: TextInputLayout
    private lateinit var regEmailEditText: TextInputEditText
    private lateinit var regPasswordLayout: TextInputLayout
    private lateinit var regPasswordEditText: TextInputEditText

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setRegistrationCallbacks(this)
        viewModel.setOverallCallbacks(this)

        regEmailLayout = binding.tilRegisterEmail
        regEmailEditText = binding.etRegisterEmail

        regPasswordLayout = binding.tilRegisterPassword
        regPasswordEditText = binding.etRegisterPassword

        setListeners()
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeSignInViewListener() {

        binding.tvSignInLabel.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupPasswordValidation() {
        regPasswordEditText.addTextChangedListener(object : TextWatcher {
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
                        regPasswordLayout.error = getString(R.string.error_password_min_length)
                    }

                    password.isNotEmpty() && password.length > Constants.MAXIMUM_PASSWORD_LENGTH -> {
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
        regEmailEditText.addTextChangedListener(object : TextWatcher {
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

    private fun initializeRegisterButtonListeners() {
        val registerButton = binding.btnRegisterButton

        registerButton.setOnClickListener {
            val email = regEmailEditText.text
            val password = regPasswordEditText.text

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
        try {
            val mainLayout: ConstraintLayout = binding.clMain

            for (i in 0 until mainLayout.childCount) {
                val view: View = mainLayout.getChildAt(i)

                if (binding.registerBackground == view) {
                    continue
                }

                view.visibility = View.VISIBLE

                val fadeInAnimation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
                view.startAnimation(fadeInAnimation)
            }
        } catch (e: NullPointerException) {
            // Ignore the exception
        }
    }

    override fun onStart() {
        super.onStart()
        hideAllViewsExceptBackground()

        Handler().postDelayed({
            fadeAllViewsExceptBackground()
        }, Constants.FADE_DELAY)

        lifecycleScope.launch {

            val accessToken = settingPreference.getAccessToken().firstOrNull()
            val refreshToken = settingPreference.getRefreshToken().firstOrNull()
            val userId = settingPreference.getUserId().firstOrNull()
            Log.d("DEBUG", "refresh token - $refreshToken")
            Log.d("DEBUG", "access token - $accessToken")
            Log.d("DEBUG", "user id - $userId")

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && userId != null) {
                viewModel.getUser(userId, accessToken)
            }
        }
    }

    override fun setObservers() {
        setupEmailValidation()
        setupPasswordValidation()
    }

    override fun setListeners() {
        initializeRegisterButtonListeners()
        initializeSignInViewListener()
    }

    override fun onEmailTakenError() {
        regEmailLayout.error = getString(R.string.error_email_exists)
    }

    override fun onSuccess(accessToken: String, refreshToken: String, userId: Int) {
        val rememberMeCheckbox = binding.chbRememberMeRegister

        if (rememberMeCheckbox.isChecked) {
            lifecycleScope.launch {
                settingPreference.saveAccessToken(accessToken)
                settingPreference.saveRefreshToken(refreshToken)
                settingPreference.saveUserId(userId)
            }
            Log.d("DEBUG", "Saved id - $userId, saved access - $accessToken, saved refresh - $refreshToken")
        }

        sessionManager.setId(userId)
        sessionManager.setAccessToken(accessToken)
        sessionManager.setRefreshToken(refreshToken)
        sessionManager.setUserRememberState(rememberMeCheckbox.isChecked)

        findNavController().navigate(R.id.action_registerFragment_to_profileDataFragment)
    }

    override fun onUserIsRemembered() {

        lifecycleScope.launch {
            sessionManager.setId(settingPreference.getUserId().first())
            sessionManager.setAccessToken(settingPreference.getAccessToken().first())
            sessionManager.setRefreshToken(settingPreference.getRefreshToken().first())
        }

        findNavController().navigate(R.id.viewPagerFragment)
    }

    override fun onTokensRefreshFailure() {
        findNavController().navigate(R.id.loginFragment)
    }
}