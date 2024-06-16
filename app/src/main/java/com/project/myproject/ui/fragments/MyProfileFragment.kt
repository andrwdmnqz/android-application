package com.project.myproject.ui.fragments


import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.databinding.FragmentMyProfileBinding
import com.project.myproject.ui.viewmodels.LoginState
import com.project.myproject.utils.extensions.loadImageByGlide
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MyProfileFragment : BaseFragment<FragmentMyProfileBinding>(FragmentMyProfileBinding::inflate) {

    private lateinit var animation: Transition
    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        setupUserData()
        setupAnimation()
    }

    private fun setupAnimation() {
        animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun setObservers() {
        observeLogoutState()
    }

    override fun setListeners() {
        initializeLogoutButtonListeners()
        initializeContactsButtonListeners()
    }

    private fun initializeLogoutButtonListeners() {
        binding.btnLogout.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.logoutUser()
            }
        }
    }

    private fun observeLogoutState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                if (state is LoginState.Idle) {
                    findNavController().navigate(R.id.action_viewPagerFragment_to_loginFragment)
                }
            }
        }
    }

    private fun initializeContactsButtonListeners() {
        binding.btnContacts.setOnClickListener {
            val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
            viewPager?.currentItem = Constants.SECOND_TAB_NUMBER
        }
    }

    private fun setupUserData() {
        val user = viewModel.getCurrentUser()

        binding.apply {
            tvNameProfile.text = user?.name ?: DEFAULT_NAME_VALUE
            tvCareerProfile.text = user?.career ?: DEFAULT_CAREER_VALUE
            tvAddressProfile.text = user?.address ?: DEFAULT_ADDRESS_VALUE
        }

        val photoView = binding.ivProfilePhoto

        if (user?.image != null) {
            photoView.loadImageByGlide(user.image!!)
        } else {
            photoView.setImageResource(R.mipmap.empty_photo_icon)
        }
    }

    companion object {
        private const val DEFAULT_NAME_VALUE = "Name"
        private const val DEFAULT_CAREER_VALUE = "Career"
        private const val DEFAULT_ADDRESS_VALUE = "Address"
    }
}