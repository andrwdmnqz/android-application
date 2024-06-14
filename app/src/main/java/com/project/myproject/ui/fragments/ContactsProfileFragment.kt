package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.databinding.FragmentContactsProfileBinding
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.extensions.loadImageByGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsProfileFragment :
    BaseFragment<FragmentContactsProfileBinding>(FragmentContactsProfileBinding::inflate) {

    private val viewModel by activityViewModels<UserViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setObservers()
        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }
    override fun setObservers() {
        setupFields()

        observeAddContactState()
    }

    private fun observeAddContactState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contactAdded.collect { contactAdded ->
                if (contactAdded) {
                    onContactAdded()
                }
            }
        }
    }

    private fun setupFields() {
        val user = ContactsProfileFragmentArgs.fromBundle(requireArguments()).user

        with (binding) {
            val image = user.image

            if (image != null) {
                ivContactProfileImage.loadImageByGlide(image)
            } else {
                ivContactProfileImage.setImageResource(R.mipmap.empty_photo_icon)
            }

            tvName.text = user.name.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_NAME_VALUE
            tvCareer.text = user.career.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_CAREER_VALUE
            tvAddress.text = user.address.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_ADDRESS_VALUE
            btnAddToMyContacts.isEnabled = true
        }
    }

    override fun setListeners() {
        binding.ivToolbarBack.setOnClickListener {
            it.findNavController().popBackStack()
        }

        binding.btnAddToMyContacts.setOnClickListener {
            viewModel.addContact(ContactsProfileFragmentArgs.fromBundle(requireArguments()).user.id)
        }
    }

    private fun onContactAdded() {
        binding.btnAddToMyContacts.isEnabled = false

        Snackbar.make(
            binding.root,
            getString(R.string.contact_added), Snackbar.LENGTH_LONG
        ).show()
        viewModel.resetContactAdded()
    }
}