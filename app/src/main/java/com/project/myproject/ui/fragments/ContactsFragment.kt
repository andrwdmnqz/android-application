package com.project.myproject.ui.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.utils.Constants
import com.project.myproject.R
import com.project.myproject.data.models.Contact
import com.project.myproject.ui.adapters.ContactAdapter
import com.project.myproject.utils.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.FragmentContactsBinding
import com.project.myproject.ui.fragments.utils.CustomAdapterDataObserver
import com.project.myproject.ui.fragments.utils.SearchTextQueryListener
import com.project.myproject.utils.DefaultItemDecorator
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class ContactsFragment : BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate),
    ContactAdapter.OnContactItemClickListener {

    private val viewModel by activityViewModels<UserViewModel>()
    private lateinit var adapter: ContactAdapter
    private lateinit var animation: Transition
    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = activity?.findViewById(R.id.viewPager)!!
        adapter = ContactAdapter(requireContext(), this) { show -> showMultiselectDelete(show) }

        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
        setupAnimation()
    }

    private fun setupRecyclerView() {
        val contactsRV = binding.rvContacts

        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        setupAdapterScroll(contactsRV)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(DefaultItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback { contact ->
            viewModel.deleteContact(contact.id)

            showDeleteSnackbar(contact)
        })

        itemTouchHelper.attachToRecyclerView(contactsRV)

        viewModel.fetchContacts()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.contacts.collect { contacts ->
                adapter.submitList(contacts)
            }
        }
    }

    private fun setupSearchFunctionality() {
        setupSearchView()
        setupSearchButtonListeners()
    }

    private fun setupSearchView() {
        binding.searchViewContacts.setOnQueryTextListener(SearchTextQueryListener { newText ->
            filter(newText)
        })
    }

    private fun setupSearchButtonListeners() {
        val searchButton = binding.ivToolbarSearch
        val searchView = binding.searchViewContacts
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

    override fun onStart() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            if (isFirstTab()) {
                activity?.finish()
                exitProcess(0)
            } else {
                moveToFirstTab()
            }
        }

        super.onStart()
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
        setupBackArrowListeners()
        setupAddContactListeners()
        setupMultiselectDeleteListeners()
        setupSearchButtonListeners()
    }

    private fun setupBackArrowListeners() {
        binding.ivToolbarBack.setOnClickListener {
            moveToFirstTab()
        }
    }

    private fun moveToFirstTab() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        viewPager?.currentItem = Constants.FIRST_TAB_NUMBER
    }

    private fun isFirstTab() = viewPager.currentItem == Constants.FIRST_TAB_NUMBER

    private fun setupAddContactListeners() {
        binding.tvAddContactsLabel.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_addContactsFragment)
        }
    }

    override fun onContactItemClicked(contact: Contact) {
        val extras = FragmentNavigatorExtras(binding.contactsBackground to "detailBackground")

        findNavController().navigate(
            ViewPagerFragmentDirections.actionViewPagerFragmentToDetailViewFragment(
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