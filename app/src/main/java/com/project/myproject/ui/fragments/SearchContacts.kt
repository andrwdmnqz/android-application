package com.project.myproject.ui.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.models.Contact
import com.project.myproject.databinding.FragmentSearchContactsBinding
import com.project.myproject.ui.adapters.ContactAdapter
import com.project.myproject.ui.fragments.utils.CustomAdapterDataObserver
import com.project.myproject.ui.fragments.utils.SearchTextQueryListener
import com.project.myproject.ui.viewmodels.UserViewModel
import com.project.myproject.utils.Constants
import com.project.myproject.utils.DefaultItemDecorator
import kotlinx.coroutines.launch

class SearchContacts : BaseFragment<FragmentSearchContactsBinding>(FragmentSearchContactsBinding::inflate),
    ContactAdapter.OnContactItemClickListener {

    private val viewModel by activityViewModels<UserViewModel>()
    private lateinit var adapter: ContactAdapter
    private lateinit var animation: Transition

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ContactAdapter(requireContext(), this) { show -> showMultiselectDelete(show) }

        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
        setupAnimation()
    }

    private fun setupRecyclerView() {
        binding.rvContacts.apply {
            setupAdapterScroll(this)
            adapter = this@SearchContacts.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DefaultItemDecorator(resources.getDimensionPixelSize(R.dimen.contacts_item_margin)))
        }
        viewModel.fetchContacts()
        collectContactsData()
    }

    private fun collectContactsData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                adapter.submitList(contacts)
            }
        }
    }

    private fun setupSearchFunctionality() {
        setupSearchView()
        setupClearButtonListeners()
    }

    private fun setupSearchView() {
        binding.searchViewContacts.setOnQueryTextListener(SearchTextQueryListener { newText ->
            filter(newText)
        })
    }

    private fun setupClearButtonListeners() {
        val clearButton = binding.ivToolbarClear
        val searchView = binding.searchViewContacts

        clearButton.setOnClickListener {
            searchView.setQuery("", false)
        }
    }

    override fun setObservers() {
        observeLoadingState()
    }

    private fun observeLoadingState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.loading.collect { isLoading ->
                binding.pbContactsList.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }
    }

    override fun setListeners() {
        setupAddContactListeners()
        setupMultiselectDeleteListeners()
        setupClearButtonListeners()
    }

    private fun setupAddContactListeners() {
        binding.tvAddContactsLabel.setOnClickListener {
            findNavController().navigate(R.id.action_searchContacts_to_addContactsFragment)
        }
    }

    override fun onContactItemClicked(contact: Contact) {
        val extras = FragmentNavigatorExtras(binding.contactsBackground to "detailBackground")

        findNavController().navigate(
            SearchContactsDirections.actionSearchContactsToDetailViewFragment(
                contact
            ), extras
        )
    }

    override fun onDeleteItemClicked(contact: Contact) {
        viewModel.deleteContact(contact.id)

        showDeleteSnackbar(contact)
    }

    private fun showDeleteSnackbar(contact: Contact) {
        val snackbar = Snackbar.make(binding.root,
            getString(R.string.contact_removed), Snackbar.LENGTH_LONG)

        snackbar.setAction(getString(R.string.undo_button)) {
            viewModel.addContact(contact.id)
        }

        snackbar.show()
    }

    private fun filter(text: String) {
        if (text == "") {
            adapter.submitList(viewModel.contacts.value)
        } else {
            val filteredList = viewModel.contacts.value.filter {
                (it.name?.contains(text, true) == true ||
                        it.career?.contains(text, true) == true) ||
                        (it.name == null && Constants.DEFAULT_NAME_VALUE.contains(text, true) ||
                                it.career == null && Constants.DEFAULT_CAREER_VALUE.contains(text, true))
            }
            adapter.submitList(filteredList)
        }
    }

    private fun setupAdapterScroll(contactsRV: RecyclerView) {
        adapter.registerAdapterDataObserver(CustomAdapterDataObserver(contactsRV))
    }

    private fun setupAnimation() {
        animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    private fun setupMultiselectDeleteListeners() {
        binding.ivMultiselectDeleteIcon.setOnClickListener {
            val selectedItems = adapter.getSelectedItems().sortedDescending()
            selectedItems.forEach {
                viewModel.deleteContact(viewModel.contacts.value[it].id)
            }

            adapter.exitMultiselectMode()
        }
    }

    private fun showMultiselectDelete(show: Boolean) {
        binding.ivMultiselectDeleteIcon.isVisible = show
    }
}