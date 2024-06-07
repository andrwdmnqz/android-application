package com.project.myproject.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentProfileDataBinding
import com.project.myproject.ui.fragments.watchers.NameTextWatcher
import com.project.myproject.ui.fragments.watchers.PhoneTextWatcher
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.EditCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ProfileDataFragment :
    BaseFragment<FragmentProfileDataBinding>(FragmentProfileDataBinding::inflate),
    EditCallbacks {

    @Inject
    lateinit var settingPreference: SettingPreference

    @Inject
    lateinit var sessionManager: SessionManager

    private val viewModel by activityViewModels<UserViewModel>()

    private lateinit var dataNameLayout: TextInputLayout
    private lateinit var dataNameEditText: TextInputEditText
    private lateinit var dataPhoneLayout: TextInputLayout
    private lateinit var dataPhoneEditText: TextInputEditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewModel.setEditCallbacks(this)

        dataNameLayout = binding.tilProfileName
        dataNameEditText = binding.etProfileName
        dataPhoneLayout = binding.tilMobilePhone
        dataPhoneEditText = binding.etMobilePhone

        setListeners()
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun initializeCancelButtonListeners() {
        val cancelButton = binding.btnCancelProfileData

        cancelButton.setOnClickListener {
            findNavController().navigate(R.id.action_profileDataFragment_to_registerFragment)
        }
    }

    private fun initializeForwardButtonListeners() {

        binding.btnForward.setOnClickListener {
            val userNameText = dataNameEditText.text
            val phoneNumber = dataPhoneEditText.text

            if (dataNameLayout.error == null && dataPhoneLayout.error == null
                && !userNameText.isNullOrBlank() && !phoneNumber.isNullOrBlank()
            ) {
                lifecycleScope.launch {
                    val userId = sessionManager.getId()
                    viewModel.editUserNameAndPhone(
                        userId,
                        userNameText.toString(), phoneNumber.toString()
                    )
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
        dataNameEditText.addTextChangedListener(NameTextWatcher(dataNameLayout, requireContext()))
    }

    private fun setupPhoneValidation() {
        dataPhoneEditText.addTextChangedListener(
            PhoneTextWatcher(
                dataPhoneLayout,
                dataPhoneEditText,
                requireContext()
            )
        )
    }
}