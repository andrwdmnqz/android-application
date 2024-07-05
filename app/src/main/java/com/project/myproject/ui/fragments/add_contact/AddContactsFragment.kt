package com.project.myproject.ui.fragments.add_contact

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavDeepLinkBuilder
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.R
import com.project.myproject.data.room.entities.User
import com.project.myproject.databinding.FragmentAddContactsBinding
import com.project.myproject.ui.fragments.add_contact.adapter.UserAdapter
import com.project.myproject.ui.fragments.add_contact.viewmodel.AddContactsViewModel
import com.project.myproject.ui.fragments.base.BaseFragment
import com.project.myproject.ui.fragments.utils.NotificationUtils
import com.project.myproject.ui.fragments.viewmodel.SharedViewModel
import com.project.myproject.utils.DefaultItemDecorator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AddContactsFragment :
    BaseFragment<FragmentAddContactsBinding>(FragmentAddContactsBinding::inflate),
    UserAdapter.OnUserItemCLickListener {

    private val viewModel: AddContactsViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()

    private lateinit var adapter: UserAdapter
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                showSearchNotification(requireContext())
            } else {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.notification_permission_required),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = UserAdapter(this)

        createNotificationChannel()
        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
    }

    private fun createNotificationChannel() {
        NotificationUtils.createNotificationChannel(
            requireContext(),
            NOTIFICATION_CHANNEL_ID,
            NOTIFICATION_CHANNEL_NAME,
            NOTIFICATION_CHANNEL_DESC
        )
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
            sharedViewModel.contactsId.collect { contactId ->
                viewModel.setContactId(contactId)
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
        setupSearchButtonListeners()
    }

    private fun setupSearchButtonListeners() {
        binding.ivToolbarSearch.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestNotificationPermission()
            } else {
                showSearchNotification(requireContext())
            }
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    private fun showSearchNotification(context: Context) {

        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.nav_graph)
            .setDestination(R.id.searchUsers)
            .createPendingIntent()


        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.search_icon)
            .setContentTitle(NOTIFICATION_CONTENT_TITLE)
            .setContentText(NOTIFICATION_CONTENT_TEXT)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        try {
            with(NotificationManagerCompat.from(context)) {
                notify(1, notification)
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
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

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "Notification channel"
        private const val NOTIFICATION_CHANNEL_DESC = "Notification channel description"
        private const val NOTIFICATION_CHANNEL_ID= "notification_channel_id"
        private const val NOTIFICATION_CONTENT_TITLE = "Search"
        private const val NOTIFICATION_CONTENT_TEXT = "Click to search"
    }
}