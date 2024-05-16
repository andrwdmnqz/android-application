package com.project.myproject.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.databinding.FragmentContactsProfileBinding
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.AddContactCallbacks
import com.project.myproject.utils.extensions.loadImageByGlide
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ContactsProfileFragment :
    BaseFragment<FragmentContactsProfileBinding>(FragmentContactsProfileBinding::inflate), AddContactCallbacks {

    private val viewModel by activityViewModels<UserViewModel>()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setAddContactsCallbacks(this)

        setObservers()
        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }
    override fun setObservers() {
        setupFields()
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
            viewModel.addContact(
                sessionManager.getId(),
                ContactsProfileFragmentArgs.fromBundle(requireArguments()).user.id,
                sessionManager.getAccessToken()
            )
        }
    }

    override fun onContactAdded() {
        Log.d("DEBUG", "Contact added callback")
        binding.btnAddToMyContacts.isEnabled = false

        Snackbar.make(
            binding.root,
            getString(R.string.contact_added), Snackbar.LENGTH_LONG
        ).show()
        Log.d("DEBUG", "After snackbar")
    }
}