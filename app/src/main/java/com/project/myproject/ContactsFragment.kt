package com.project.myproject

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.FragmentContactsBinding
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.dialogs.AddContactDialogFragment
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class ContactsFragment : Fragment(R.layout.fragment_contacts) {

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var userViewModel: UserViewModel
    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)

        settingPreference = SettingPreference(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupAnimation()

        val adapter = UserAdapter(ArrayList())

        setupAddContacts(adapter)

        setupRecyclerView(adapter)

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAnimation() {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupAddContacts(adapter: UserAdapter) {
        val addContactsView = binding.addContactsLabel

        addContactsView.setOnClickListener {
            AddContactDialogFragment(adapter).show(
                childFragmentManager, AddContactDialogFragment.TAG
            )
        }
    }

    private fun setupRecyclerView(adapter: UserAdapter) {
        val contactsRV = binding.rvContacts
        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(adapter))
        itemTouchHelper.attachToRecyclerView(contactsRV)

        adapter.recyclerView = contactsRV

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel.init()

        lifecycleScope.launch {
            userViewModel.users.collect { users ->
                adapter.contactsList = users
                adapter.notifyDataSetChanged()
            }
        }
    }
}