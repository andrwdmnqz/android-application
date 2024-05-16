package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.models.Contact
import com.project.myproject.data.models.User
import com.project.myproject.databinding.FragmentAddContactsBinding
import com.project.myproject.ui.adapters.UserAdapter
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.DefaultItemDecorator
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.AddContactCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddContactsFragment :
    BaseFragment<FragmentAddContactsBinding>(FragmentAddContactsBinding::inflate),
    UserAdapter.OnUserItemCLickListener, AddContactCallbacks {

    private val viewModel by activityViewModels<UserViewModel>()

    private lateinit var adapter: UserAdapter

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.setAddContactsCallbacks(this)
        adapter = UserAdapter(this)

        setupRecyclerView()
        setListeners()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        viewModel.fetchUsers(sessionManager.getAccessToken())

        val usersRV = binding.rvUsers
        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        usersRV.adapter = adapter
        usersRV.addItemDecoration(DefaultItemDecorator(itemMarginSize))
        usersRV.layoutManager = LinearLayoutManager(requireContext())

        lifecycleScope.launch {
            viewModel.users.combine(viewModel.contactsId) { users, contactsId ->
                users to contactsId
            }.collect { (users, contactsId) ->
                adapter.submitList(users)
                adapter.updateContactsIds(contactsId)
            }
        }

    }

    override fun onUserItemClicked(user: User) {

        if (user.id !in viewModel.contactsId.value) {
            findNavController().navigate(
                AddContactsFragmentDirections.actionAddContactsFragmentToContactsProfileFragment(
                    user
                )
            )
        } else {
            findNavController().navigate(
                AddContactsFragmentDirections.actionAddContactsFragmentToDetailViewFragment(
                    Contact(
                        user.id,
                        user.name,
                        user.email,
                        user.phone,
                        user.career,
                        user.address,
                        user.birthday,
                        user.facebook,
                        user.instagram,
                        user.twitter,
                        user.linkedin,
                        user.image,
                        user.createdAt,
                        user.updatedAt,
                        false
                    )
                )
            )
        }
    }

    override fun onAddItemClicked(user: User, position: Int) {
        if (user.id !in viewModel.contactsId.value) {
            viewModel.addContact(sessionManager.getId(), user.id, sessionManager.getAccessToken())
        }
    }

    override fun setObservers() {
        // Not used
    }

    override fun setListeners() {
        binding.ivToolbarBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }

    override fun onContactAdded() {
        Snackbar.make(
            binding.root,
            getString(R.string.contact_added), Snackbar.LENGTH_LONG
        ).show()
    }
}