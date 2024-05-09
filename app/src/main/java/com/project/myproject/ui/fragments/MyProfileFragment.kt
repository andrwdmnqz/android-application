package com.project.myproject.ui.fragments


import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    @Inject
    lateinit var settingPreference: SettingPreference

    private lateinit var animation: Transition

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setListeners()
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
        // Not used
    }

    override fun setListeners() {
        initializeLogoutButtonListeners()
        initializeContactsButtonListeners()
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