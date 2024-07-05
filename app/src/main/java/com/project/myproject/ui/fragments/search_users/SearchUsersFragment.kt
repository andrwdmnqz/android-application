package com.project.myproject.ui.fragments.search_users

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.room.entities.User
import com.project.myproject.databinding.FragmentSearchUsersBinding
import com.project.myproject.ui.fragments.add_contact.AddContactsFragmentDirections
import com.project.myproject.ui.fragments.add_contact.adapter.UserAdapter
import com.project.myproject.ui.fragments.base.BaseFragment
import com.project.myproject.ui.fragments.search_users.viewmodel.SearchUsersViewModel
import com.project.myproject.ui.fragments.utils.SearchTextQueryListener
import com.project.myproject.ui.fragments.viewmodel.SharedViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.DefaultItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchUsersFragment :
    BaseFragment<FragmentSearchUsersBinding>(FragmentSearchUsersBinding::inflate),
    UserAdapter.OnUserItemCLickListener {

    private val viewModel: SearchUsersViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var adapter: UserAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(this)
        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
    }

    private fun setupRecyclerView() {
        binding.rvUsers.apply {
            adapter = this@SearchUsersFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DefaultItemDecorator(resources.getDimensionPixelSize(R.dimen.contacts_item_margin)))
        }
        viewModel.fetchUsers()
        collectUserData()
    }

    private fun collectUserData() {
        viewModel.fetchContacts()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.currentUser.collect { user ->
                sharedViewModel.setCurrentUser(user)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.users.combine(viewModel.contactsId) { users, contactsId ->
                users to contactsId
            }.collect { (users, contactsId) ->
                adapter.submitList(users)
                adapter.updateContactsIds(contactsId)
            }
        }
    }

    private fun setupSearchFunctionality() {
        setupClearButtonListeners()
        setupSearchView()
    }

    private fun setupClearButtonListeners() {
        val clearButton = binding.ivToolbarClear
        val searchView = binding.searchViewUsers

        clearButton.setOnClickListener {
            searchView.setQuery("", false)
        }
    }

    private fun setupSearchView() {
        binding.searchViewUsers.setOnQueryTextListener(SearchTextQueryListener { newText ->
            filter(newText)
        })
    }

    private fun filter(text: String) {
        if (text == "") {
            adapter.submitList(viewModel.users.value)
        } else {
            val filteredList = viewModel.users.value.filter {
                (it.name?.contains(text, true) == true ||
                        it.career?.contains(text, true) == true) ||
                        (it.name == null && Constants.DEFAULT_NAME_VALUE.contains(text, true) ||
                                it.career == null && Constants.DEFAULT_CAREER_VALUE.contains(text, true))
            }
            adapter.submitList(filteredList)
        }
    }

    override fun onUserItemClicked(user: User) {

        if (user.id !in viewModel.contactsId.value) {
            findNavController().navigate(
                SearchUsersFragmentDirections.actionSearchUsersToContactsProfileFragment(
                    user
                )
            )
        } else {
            findNavController().navigate(
                AddContactsFragmentDirections.actionAddContactsFragmentToDetailViewFragment(
                    user.toContact()
                )
            )
        }
    }

    override fun onAddItemClicked(user: User, position: Int) {
        if (user.id !in viewModel.contactsId.value) {
            viewModel.addContact(user.id)
        }
    }

    override fun setObservers() {
        observeLoadingState()
        observeAddContactState()
    }

    private fun observeLoadingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { isLoading ->
                    binding.pbAddContacts.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
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
            viewModel.contactsId.collect { contactsId ->
                sharedViewModel.setContactsId(contactsId)
            }
        }
    }

    override fun setListeners() {
        setupClearButtonListeners()
    }

    private fun onContactAdded() {
        Snackbar.make(
            binding.root,
            getString(R.string.contact_added), Snackbar.LENGTH_LONG
        ).show()
        viewModel.resetContactAdded()
    }
}