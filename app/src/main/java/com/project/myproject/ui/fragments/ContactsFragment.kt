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
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.activity.addCallback
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

        createNotificationChannel(requireContext())
        setupRecyclerView()
        setupSearchFunctionality()
        setListeners()
        setObservers()
        setupAnimation()
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
        binding.rvContacts.apply {
            setupAdapterScroll(this)
            adapter = this@ContactsFragment.adapter
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DefaultItemDecorator(resources.getDimensionPixelSize(R.dimen.contacts_item_margin)))

            val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback { contact ->
                viewModel.deleteContact(contact.id)
                showDeleteSnackbar(contact)
            })

            itemTouchHelper.attachToRecyclerView(this)
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
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                Constants.NOTIFICATION_PERMISSION_REQUEST_CODE
            )
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
        val uri = Uri.parse(NOTIFICATION_CONTENT_URI)
        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

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

    companion object {
        private const val NOTIFICATION_CHANNEL_NAME = "Notification channel"
        private const val NOTIFICATION_CHANNEL_DESC = "Notification channel description"
        private const val NOTIFICATION_CHANNEL_ID= "notification_channel_id"
        private const val NOTIFICATION_CONTENT_TITLE = "Search"
        private const val NOTIFICATION_CONTENT_TEXT = "Click to search"
        private const val NOTIFICATION_CONTENT_URI = "myapp://search/contacts"
    }
}