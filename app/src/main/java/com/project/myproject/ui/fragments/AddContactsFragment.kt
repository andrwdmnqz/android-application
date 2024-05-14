package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.models.User
import com.project.myproject.databinding.FragmentAddContactsBinding
import com.project.myproject.ui.adapters.UserAdapter
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.DefaultItemDecorator
import com.project.myproject.utils.SessionManager
import com.project.myproject.utils.callbacks.AddContactCallbacks
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class AddContactsFragment : BaseFragment<FragmentAddContactsBinding>(FragmentAddContactsBinding::inflate),
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
        val usersRV = binding.rvUsers

        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)
        // TODO - mb not needed
        //setupAdapterScroll(usersRV)

        usersRV.adapter = adapter
        usersRV.addItemDecoration(DefaultItemDecorator(itemMarginSize))
        usersRV.layoutManager = LinearLayoutManager(requireContext())

        viewModel.fetchUsers(sessionManager.getAccessToken())

        lifecycleScope.launch {
            viewModel.users.collect { users ->
                adapter.submitList(users)
            }
        }
    }

    private fun setupAdapterScroll(contactsRV: RecyclerView) {
        adapter.registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                // No need to scroll here
            }
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                // No need to scroll here
            }
            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                // No need to scroll here
            }
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (itemCount == 1 && positionStart == 0) {
                    contactsRV.scrollToPosition(positionStart)
                }
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                // No need to scroll here
            }
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                // No need to scroll here
            }
        })
    }

    override fun onUserItemClicked(user: User) {
        findNavController().navigate(
            AddContactsFragmentDirections.actionAddContactsFragmentToContactsProfileFragment(
                user
            )
        )
    }

    override fun onAddItemClicked(user: User) {
        viewModel.addContact(sessionManager.getId(), user.id, sessionManager.getAccessToken())
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