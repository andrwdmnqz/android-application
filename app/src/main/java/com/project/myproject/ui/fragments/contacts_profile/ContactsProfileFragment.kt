package com.project.myproject.ui.fragments.contacts_profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.databinding.FragmentContactsProfileBinding
import com.project.myproject.ui.fragments.base.BaseFragment
import com.project.myproject.ui.fragments.contacts_profile.viewmodel.ContactsProfileViewModel
import com.project.myproject.ui.fragments.viewmodel.SharedViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.extensions.loadImageByGlide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ContactsProfileFragment :
    BaseFragment<FragmentContactsProfileBinding>(FragmentContactsProfileBinding::inflate) {

    private val viewModel: ContactsProfileViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setObservers()
        setListeners()
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
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                sharedViewModel.setContacts(contacts)
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contactsId.collect { contactId ->
                sharedViewModel.setContactsId(contactId)
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
        setBackArrowListener()
        setAddContactIconListener()
    }

    private fun setAddContactIconListener() {
        binding.btnAddToMyContacts.setOnClickListener {
            viewModel.addContact(ContactsProfileFragmentArgs.fromBundle(requireArguments()).user.id)
        }
    }

    private fun setBackArrowListener() {
        binding.ivToolbarBack.setOnClickListener {
            it.findNavController().navigateUp()
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