package com.project.myproject.ui.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.databinding.FragmentRegisterBinding
import com.project.myproject.ui.fragments.watchers.EmailTextWatcher
import com.project.myproject.ui.fragments.watchers.PasswordTextWatcher
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.SettingPreference
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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            binding.btnGoogleRegister.setContent {
                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonColors(
                        containerColor = Color.White,
                        contentColor = Color.White,
                        disabledContainerColor = Color.White,
                        disabledContentColor = Color.White,
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.google_logo),
                        contentDescription = "google logo"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(id = R.string.google_text),
                        color = Color.Black
                    )
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setRegistrationCallbacks(this)
        viewModel.setOverallCallbacks(this)

        regEmailLayout = binding.tilRegisterEmail
        regEmailEditText = binding.etRegisterEmail

        regPasswordLayout = binding.tilRegisterPassword
        regPasswordEditText = binding.etRegisterPassword

        setListeners()
        setObservers()

        hideAllViewsExceptBackground()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeSignInViewListener() {

        binding.tvSignInLabel.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    private fun setupPasswordValidation() {
        regPasswordEditText.addTextChangedListener(PasswordTextWatcher(regPasswordLayout, requireContext()))
    }

    private fun setupEmailValidation() {
        regEmailEditText.addTextChangedListener(EmailTextWatcher(regEmailLayout, requireContext()))
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
        viewLifecycleOwner.lifecycleScope.launch {
            val mainLayout: ConstraintLayout = binding.clMain

            for (i in 0 until mainLayout.childCount) {
                val view: View = mainLayout.getChildAt(i)

                if (binding.registerBackground == view) {
                    continue
                }

                view.visibility = View.VISIBLE

                val fadeInAnimation =
                    AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
                view.startAnimation(fadeInAnimation)
            }
        }
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            if (view != null) {
                fadeAllViewsExceptBackground()
            }
        }, FADE_DELAY)

        viewLifecycleOwner.lifecycleScope.launch {

            val accessToken = settingPreference.getAccessToken().firstOrNull()
            val refreshToken = settingPreference.getRefreshToken().firstOrNull()
            val userId = settingPreference.getUserId().firstOrNull()

            if (!accessToken.isNullOrBlank() && !refreshToken.isNullOrBlank() && userId != null && userId != -1) {
                sessionManager.setupData(userId, accessToken, refreshToken, true)
                viewModel.getUser(userId)
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
            viewLifecycleOwner.lifecycleScope.launch {
                settingPreference.saveAccessToken(accessToken)
                settingPreference.saveRefreshToken(refreshToken)
                settingPreference.saveUserId(userId)
            }
        }

        sessionManager.setupData(userId, accessToken, refreshToken, rememberMeCheckbox.isChecked)

        findNavController().navigate(R.id.action_registerFragment_to_profileDataFragment)
    }

    override fun onUserIsRemembered() {

        viewLifecycleOwner.lifecycleScope.launch {
            sessionManager.setId(settingPreference.getUserId().first())
            sessionManager.setAccessToken(settingPreference.getAccessToken().first())
            sessionManager.setRefreshToken(settingPreference.getRefreshToken().first())
            sessionManager.setUserRememberState(true)
        }

        findNavController().navigate(R.id.viewPagerFragment)
    }

    override fun onUserIsNotRemembered() {
        sessionManager.resetData()
    }

    override fun onTokensRefreshFailure() {
        sessionManager.resetData()
        lifecycleScope.launch {
            settingPreference.clearData()
        }
        findNavController().navigate(R.id.loginFragment)
    }

    companion object {
        private const val FADE_DELAY = 1500L
    }
}