package com.project.myproject.ui.fragments


import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.utils.SettingPreference
import com.project.myproject.databinding.FragmentMyProfileBinding
import com.project.myproject.utils.extensions.loadImageByGlide
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    private lateinit var settingPreference: SettingPreference

    private lateinit var animation: Transition

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        settingPreference = SettingPreference(requireContext())

        initializeLogoutButtonListeners()
        initializeContactsButtonListeners()
        setupUserData()
        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAnimation() {

        animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun setObservers() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    private fun initializeLogoutButtonListeners() {

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                settingPreference.clearData()

                it.findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
            }
        }
    }

    private fun initializeContactsButtonListeners() {

        binding.contactsButton.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.currentItem = Constants.SECOND_TAB_NUMBER
        }
    }

    private fun setupUserData() {
        val photoView = binding.ivProfilePhoto
        val nameView = binding.tvNameProfile
        val careerView = binding.tvCareerProfile
        val addressView = binding.tvAddressProfile

        val user = viewModel.getCurrentUser()

        if (user?.image != null) {
            photoView.loadImageByGlide(user.image!!)
        } else {
            photoView.setImageResource(R.mipmap.empty_photo_icon)
        }

        nameView.text = user?.name ?: "Name"
        careerView.text = user?.career ?: "Career"
        addressView.text = user?.address ?: "Address"
    }
}