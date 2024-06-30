package com.project.myproject.ui.fragments

import android.os.Bundle
import android.os.Handler
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
import com.project.myproject.R
import com.project.myproject.databinding.FragmentRegisterBinding
import com.project.myproject.ui.fragments.utils.EmailTextWatcher
import com.project.myproject.ui.fragments.utils.PasswordTextWatcher
import com.project.myproject.ui.viewmodels.RegistrationState
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.callbacks.TokenCallbacks
import kotlinx.coroutines.launch

class RegisterFragment :
    BaseFragment<FragmentRegisterBinding>(FragmentRegisterBinding::inflate), TokenCallbacks {

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState).also {
            setupGoogleRegisterButton()
        }
    }

    private fun setupGoogleRegisterButton() {
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        setListeners()
        setObservers()

        hideAllViewsExceptBackground()
    }

    private fun initializeUI() {
        with(binding) {
            tilRegisterEmail.editText?.addTextChangedListener(EmailTextWatcher(tilRegisterEmail, requireContext()))
            tilRegisterPassword.editText?.addTextChangedListener(PasswordTextWatcher(tilRegisterPassword, requireContext()))
        }
    }

    override fun setListeners() {
        binding.tvSignInLabel.setOnClickListener {
            navigateToSignIn()
        }

        binding.btnRegisterButton.setOnClickListener {
            attemptUserRegistration()
        }
    }

    private fun navigateToSignIn() {
        findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
    }

    private fun attemptUserRegistration() {
        val email = binding.etRegisterEmail.text.toString()
        val password = binding.etRegisterPassword.text.toString()
        val rememberMe = binding.chbRememberMeRegister.isChecked

        if (isValidInput(email, password)) {
            viewModel.registerUser(email, password, rememberMe)
        } else {
            showInputErrors(email, password)
        }
    }

    private fun isValidInput(email: String, password: String): Boolean {
        return binding.tilRegisterEmail.error == null && binding.tilRegisterPassword.error == null &&
                email.isNotBlank() && password.isNotBlank()
    }

    private fun showInputErrors(email: String, password: String) {
        if (email.isBlank()) {
            binding.tilRegisterEmail.error = getString(R.string.error_email_required)
        }

        if (password.isBlank()) {
            binding.tilRegisterPassword.error = getString(R.string.error_password_required)
        }
    }

    override fun setObservers() {
        observeRegisterState()
    }

    private fun observeRegisterState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.registrationState.collect { state ->
                when (state) {
                    is RegistrationState.Success -> onSuccessRegister()
                    is RegistrationState.InvalidRegisterData -> onEmailTakenError()
                    is RegistrationState.RememberedUser -> onUserIsRemembered()
                    is RegistrationState.Idle -> Unit
                }
            }
        }
    }

    private fun onSuccessRegister() {
        findNavController().navigate(R.id.action_registerFragment_to_profileDataFragment)
    }

    private fun onUserIsRemembered() {
        findNavController().navigate(R.id.viewPagerFragment)
    }

    private fun onEmailTakenError() {
        binding.tilRegisterEmail.error = getString(R.string.error_email_exists)
    }

    override fun onStart() {
        super.onStart()

        Handler().postDelayed({
            if (view != null) {
                fadeAllViewsExceptBackground()
            }
        }, FADE_DELAY)

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getUser()
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

    override fun onTokensRefreshFailure() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.logoutUser()
        }
        findNavController().navigate(R.id.loginFragment)
    }

    companion object {
        private const val FADE_DELAY = 1500L
    }
}