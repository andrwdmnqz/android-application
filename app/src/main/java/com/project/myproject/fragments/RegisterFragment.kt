package com.project.myproject.fragments

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.Constants
import com.project.myproject.R
import com.project.myproject.SettingPreference
import com.project.myproject.databinding.FragmentRegisterBinding
import com.project.myproject.network.retrofit.RetrofitService
import com.project.myproject.repository.MainRepository
import com.project.myproject.viewmodels.RegistrationCallbacks
import com.project.myproject.viewmodels.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register), RegistrationCallbacks {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    private lateinit var regEmailLayout: TextInputLayout
    private lateinit var regEmailInput: TextInputEditText
    private lateinit var regPasswordInput: TextInputEditText

    private lateinit var settingPreference: SettingPreference

    private lateinit var animation: Transition

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(layoutInflater, container, false)

        settingPreference = SettingPreference(requireContext())
        viewModel.setRegistrationCallbacks(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        regEmailLayout = binding.regEmailLayout
        val regPasswordLayout = binding.regPasswordLayout

        regEmailInput = binding.regEmailInput
        regPasswordInput = binding.regPasswordInput

        //createViewModel()
        initializeRegisterButtonListeners(regPasswordLayout)
        initializeSignInViewListener()
        setupEmailValidation()
        setupPasswordValidation(regPasswordLayout)
        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeSignInViewListener() {
        val signInView = binding.tvSignIn

        signInView.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

//    private fun createViewModel() {
//        val retrofitService = RetrofitService.getInstance()
//        val mainRepository = MainRepository(retrofitService)
//
//        viewModel = ViewModelProvider(this, UserViewModelFactory(
//            mainRepository, this))[UserViewModel::class.java]
//    }

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
        val rememberMeCheckbox = binding.rememberMe

        registerButton.setOnClickListener {
            val email = regEmailInput.text
            val password = regPasswordInput.text

            if (regEmailLayout.error == null && regPasswordLayout.error == null
                && !email.isNullOrBlank() && !password.isNullOrBlank()) {

                if (rememberMeCheckbox.isChecked) {
                    lifecycleScope.launch {
                        settingPreference.saveEmail(email.toString())
                        settingPreference.savePassword(password.toString())
                    }
                }

                viewModel.registerUser(email.toString(), password.toString())
//                val extras = FragmentNavigatorExtras(binding.registerBackground to "mainBackground")
//                it.findNavController().navigate(
//                    RegisterFragmentDirections.actionRegisterFragmentToMainFragment(
//                    regEmailInput.text.toString()), extras)
            }

            if (email.isNullOrBlank()) {
                regEmailLayout.error = getString(R.string.error_email_required)
            }

            if (password.isNullOrBlank()) {
                regPasswordLayout.error = getString(R.string.error_password_required)
            }
        }
    }

    private fun setupAnimation() {
        hideAllViewsExceptBackground()

        animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
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

        lifecycleScope.launch {
            val email = settingPreference.getEmail().firstOrNull()
            val password = settingPreference.getPassword().firstOrNull()

            if (!email.isNullOrBlank() && !password.isNullOrBlank()) {
                val extras = FragmentNavigatorExtras(binding.registerBackground to "mainBackground")
//                findNavController().navigate(
//                    RegisterFragmentDirections.actionRegisterFragmentToMainFragment(email), extras)
            }
        }
    }

    override fun onResume() {
        super.onResume()

        Handler().postDelayed({
            fadeAllViewsExceptBackground()
        }, animation.duration)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onEmailTakenError() {
        regEmailLayout.error = getString(R.string.error_email_exists)
    }

    override fun onSuccess() {
        findNavController().navigate(R.id.action_registerFragment_to_profileDataFragment)
    }
}