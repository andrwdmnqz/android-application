package com.project.myproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentProfileDataBinding
import com.project.myproject.ui.viewmodels.EditCallbacks
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ProfileDataFragment : Fragment(R.layout.fragment_profile_data), EditCallbacks {
    private var _binding: FragmentProfileDataBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingPreference: SettingPreference

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDataBinding.inflate(layoutInflater, container, false)

        settingPreference = SettingPreference(requireContext())
        viewModel.setEditCallbacks(this)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initializeForwardButtonListeners()
        initializeCancelButtonListeners()

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
            val userNameText = binding.tInputEditProfileName.text.toString()
            val phoneNumber = binding.tInputEditMobilePhone.text.toString()

            lifecycleScope.launch {
                val userId = settingPreference.getUserId().first()
                val accessToken = settingPreference.getAccessToken().first()
                Log.d("DEBUG", "Getted id - $userId, getted access - $accessToken")
                viewModel.editUser(userId,
                    accessToken,
                    userNameText, phoneNumber)
            }
        }
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onUserEdited() {
        findNavController().navigate(R.id.action_profileDataFragment_to_viewPagerFragment)
    }
}