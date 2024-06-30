package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.myproject.R
import com.project.myproject.databinding.FragmentProfileDataBinding
import com.project.myproject.ui.fragments.utils.NameTextWatcher
import com.project.myproject.ui.fragments.utils.PhoneTextWatcher
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class ProfileDataFragment :
    BaseFragment<FragmentProfileDataBinding>(FragmentProfileDataBinding::inflate) {

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initializeUI()
        setListeners()
        setObservers()
    }

    private fun initializeUI() {
        with(binding) {
            tilProfileName.editText?.addTextChangedListener(NameTextWatcher(tilProfileName,
                requireContext()))
            tilMobilePhone.editText?.addTextChangedListener(PhoneTextWatcher(tilMobilePhone,
                etMobilePhone, requireContext()))
        }
    }

    override fun setListeners() {
        binding.btnCancelProfileData.setOnClickListener {
            navigateToRegisterFragment()
        }

        binding.btnForward.setOnClickListener {
            attemptUserProfileUpdate()
        }
    }

    private fun navigateToRegisterFragment() {
        findNavController().navigate(R.id.action_profileDataFragment_to_registerFragment)
    }

    private fun attemptUserProfileUpdate() {
        val userName = binding.etProfileName.text.toString()
        val phoneNumber = binding.etMobilePhone.text.toString()

        if (isValidInput(userName, phoneNumber)) {
            viewModel.editUserNameAndPhone(userName, phoneNumber)
        } else {
            showInputErrors(userName, phoneNumber)
        }
    }

    private fun isValidInput(userName: String, phoneNumber: String): Boolean {
        return binding.tilMobilePhone.error == null && binding.tilProfileName.error == null &&
                userName.isNotBlank() && phoneNumber.isNotBlank()
    }

    private fun showInputErrors(userName: String, phoneNumber: String) {
        if (userName.isBlank()) {
            binding.tilProfileName.error = getString(R.string.error_name_required)
        }

        if (phoneNumber.isBlank()) {
            binding.tilMobilePhone.error = getString(R.string.error_phone_required)
        }
    }

    override fun setObservers() {
        observeOnUserEditedState()
    }

    private fun observeOnUserEditedState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.userEdited.collect { userEdited ->
                if (userEdited) {
                    onUserEdited()
                }
            }
        }
    }

    private fun onUserEdited() {
        viewModel.resetUserEdited()
        findNavController().navigate(R.id.action_profileDataFragment_to_viewPagerFragment)
    }
}