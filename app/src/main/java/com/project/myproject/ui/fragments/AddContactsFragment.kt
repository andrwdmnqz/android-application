package com.project.myproject.ui.fragments

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

        createNotificationChannel(requireContext())
        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
    }

    private fun createNotificationChannel(context: Context) {
        val name = NOTIFICATION_CHANNEL_NAME
        val descriptionText = NOTIFICATION_CHANNEL_DESC
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }

        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), Constants.NOTIFICATION_PERMISSION_REQUEST_CODE)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showSearchNotification(requireContext())
            }
        }
    }

    private fun showSearchNotification(context: Context) {
        val uri = Uri.parse("myapp://search/users")
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "your_channel_id")
            .setSmallIcon(R.drawable.search_icon)
            .setContentTitle("Search")
            .setContentText("Click to search")
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

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "Notification channel"
        private const val NOTIFICATION_CHANNEL_DESC = "Notification channel description"
        private const val NOTIFICATION_CHANNEL_ID= "notification_channel_id"
    }
}