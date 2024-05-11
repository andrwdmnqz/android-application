package com.project.myproject.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentProfileDataBinding
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.EditCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDataFragment : BaseFragment<FragmentProfileDataBinding>(FragmentProfileDataBinding::inflate),
    EditCallbacks {

    @Inject
    lateinit var settingPreference: SettingPreference
    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel by activityViewModels<UserViewModel>()

    private lateinit var dataNameLayout: TextInputLayout
    private lateinit var dataNameInput: TextInputEditText
    private lateinit var dataPhoneLayout: TextInputLayout
    private lateinit var dataPhoneInput: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setEditCallbacks(this)

        dataNameLayout = binding.tInputLayoutProfileName
        dataNameInput = binding.tInputEditProfileName
        dataPhoneLayout = binding.tInputLayoutMobilePhone
        dataPhoneInput = binding.tInputEditMobilePhone

        setListeners()
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeCancelButtonListeners() {
        val cancelButton = binding.buttonCancelProfileData

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileDataFragment_to_registerFragment)
        }
    }

    private fun initializeForwardButtonListeners() {

        binding.buttonForward.setOnClickListener {
            val userNameText = dataNameInput.text
            val phoneNumber = dataPhoneInput.text
            Log.d("DEBUG", "name - $userNameText")
            Log.d("DEBUG", "phone - $phoneNumber")

            if (dataNameLayout.error == null && dataPhoneLayout.error == null
                && !userNameText.isNullOrBlank() && !phoneNumber.isNullOrBlank()) {
                lifecycleScope.launch {
                    val userId = sessionManager.getId()
                    val accessToken = sessionManager.getAccessToken()
                    Log.d("DEBUG", "Getted id - $userId, getted access - $accessToken")
                    viewModel.editUserNameAndPhone(userId,
                        accessToken,
                        userNameText.toString(), phoneNumber.toString())
                }
            }

            if (userNameText.isNullOrBlank()) {
                dataNameLayout.error = getString(R.string.error_name_required)
            }

            if (phoneNumber.isNullOrBlank()) {
                dataPhoneLayout.error = getString(R.string.error_phone_required)
            }
        }
    }

    override fun setObservers() {
        setupNameValidation()
        setupPhoneValidation()
    }

    override fun setListeners() {
        initializeForwardButtonListeners()
        initializeCancelButtonListeners()
    }

    override fun onUserEdited() {
        findNavController().navigate(R.id.action_profileDataFragment_to_viewPagerFragment)
    }

    private fun setupNameValidation() {
        dataNameInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val nameAllowedSymbolsRegex = Regex(Constants.NAME_REGEX)
                val name = s.toString()
                when {
                    name.isNotEmpty() && name.length < Constants.MINIMUM_NAME_LENGTH -> {
                        dataNameLayout.error = getString(R.string.error_name_min_length)
                    }

                    name.isNotEmpty() && name.length > Constants.MAXIMUM_NAME_LENGTH -> {
                        dataNameLayout.error = getString(R.string.error_name_max_length)
                    }

                    name.isNotEmpty() && !nameAllowedSymbolsRegex.matches(name) -> {
                        dataNameLayout.error = getString(R.string.error_name_symbols)
                    }

                    else -> {
                        dataNameLayout.error = null
                    }
                }
            }
        })
    }

    private fun setupPhoneValidation() {
        dataPhoneInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }

            override fun afterTextChanged(s: Editable?) {
                val phone = s.toString().replace("[^\\d]".toRegex(), "")

                when {
                    phone.isNotEmpty() && !Regex(Constants.MINIMUM_PHONE_LENGTH_REGEX).matches(phone) -> {
                        dataPhoneLayout.error = getString(R.string.error_phone_min_length)
                    }

                    phone.isNotEmpty() && !Regex(Constants.MAXIMUM_PHONE_LENGTH_REGEX).matches(phone) -> {
                        dataPhoneLayout.error = getString(R.string.error_phone_max_length)
                    }

                    else -> {
                        dataPhoneLayout.error = null
                    }
                }

                formatPhoneNumber(phone)
            }
        })
    }

    private fun TextWatcher.formatPhoneNumber(phone: String) {
        val formattedPhoneNumber = StringBuilder()

        for (i in phone.indices) {
            when (i) {
                0 -> formattedPhoneNumber.append("(")
                3 -> formattedPhoneNumber.append(")-")
                6 -> formattedPhoneNumber.append("-")
                10 -> formattedPhoneNumber.append("-")
                else -> {
                }
            }
            formattedPhoneNumber.append(phone[i])
        }

        dataPhoneInput.removeTextChangedListener(this)
        dataPhoneInput.setText(formattedPhoneNumber.toString())
        dataPhoneInput.setSelection(formattedPhoneNumber.length)
        dataPhoneInput.addTextChangedListener(this)
    }
}