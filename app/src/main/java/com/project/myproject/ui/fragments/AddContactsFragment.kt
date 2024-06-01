package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
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
import com.project.myproject.utils.Constants
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
        setObservers()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupRecyclerView() {
        viewModel.fetchUsers()

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

    private fun setupSearchButtonListeners() {
        val searchButton = binding.ivToolbarSearch
        val searchView = binding.searchViewUsers
        val searchViewSearchIcon = binding.ivSearchViewSearchIcon

        searchButton.setOnClickListener {

            val searchIconId = R.drawable.search_icon
            val clearIconId = R.drawable.search_clear_icon
            val currentIconId = searchButton.tag as? Int ?: R.drawable.search_icon

            when (currentIconId) {
                searchIconId -> {
                    searchView.visibility = View.VISIBLE
                    searchViewSearchIcon.visibility = View.VISIBLE
                    searchButton.setImageResource(R.drawable.search_clear_icon)
                    searchButton.tag = clearIconId
                }
                else -> {
                    searchView.visibility = View.GONE
                    searchViewSearchIcon.visibility = View.GONE
                    searchButton.setImageResource(R.drawable.search_icon)
                    searchButton.tag = searchIconId
                    searchView.setQuery("", false)
                }
            }
        }
    }

    private fun setupSearchView() {
        val searchView = binding.searchViewUsers

        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    filter(newText)
                }
                return true
            }
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
            viewModel.addContact(sessionManager.getId(), user.id)
        }
    }

    override fun setObservers() {
        setupSearchView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loading.collect { isLoading ->
                    binding.pbAddContacts.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
        }
    }

    override fun setListeners() {
        setupSearchButtonListeners()

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