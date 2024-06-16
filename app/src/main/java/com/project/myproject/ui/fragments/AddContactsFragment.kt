package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.mappers.UserToContactMapper
import com.project.myproject.data.models.User
import com.project.myproject.databinding.FragmentAddContactsBinding
import com.project.myproject.ui.adapters.UserAdapter
import com.project.myproject.ui.fragments.utils.SearchTextQueryListener
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.DefaultItemDecorator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class AddContactsFragment :
    BaseFragment<FragmentAddContactsBinding>(FragmentAddContactsBinding::inflate),
    UserAdapter.OnUserItemCLickListener {

    private val viewModel by activityViewModels<UserViewModel>()

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
            adapter = this@AddContactsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DefaultItemDecorator(resources.getDimensionPixelSize(R.dimen.contacts_item_margin)))
        }
        viewModel.fetchUsers()
        collectUserData()
    }

    private fun collectUserData() {
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
        setupSearchButtonListeners()
        setupSearchView()
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
                AddContactsFragmentDirections.actionAddContactsFragmentToContactsProfileFragment(
                    user
                )
            )
        } else {
            findNavController().navigate(
                AddContactsFragmentDirections.actionAddContactsFragmentToDetailViewFragment(
                    UserToContactMapper.map(user)
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
    }

    override fun setListeners() {
        setupSearchButtonListeners()
        setBackArrowListener()
    }

    private fun setBackArrowListener() {
        binding.ivToolbarBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    private fun onContactAdded() {
        Snackbar.make(
            binding.root,
            getString(R.string.contact_added), Snackbar.LENGTH_LONG
        ).show()
        viewModel.resetContactAdded()
    }
}