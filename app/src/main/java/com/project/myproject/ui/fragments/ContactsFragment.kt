package com.project.myproject.ui.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
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
import com.project.myproject.ui.adapters.UserAdapter
import com.project.myproject.utils.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.FragmentContactsBinding
import com.project.myproject.utils.UserItemDecorator
import com.project.myproject.ui.dialogs.AddContactDialogFragment
import com.project.myproject.data.models.User
import com.project.myproject.ui.viewmodels.UserViewModel
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

class ContactsFragment : BaseFragment<FragmentContactsBinding>(FragmentContactsBinding::inflate),
    UserAdapter.OnUserItemClickListener {
    private val viewModel: UserViewModel by viewModels<UserViewModel>()

    private lateinit var adapter: UserAdapter

    private lateinit var animation: Transition

    private lateinit var viewPager: ViewPager2

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewPager = activity?.findViewById(R.id.viewPager)!!
        adapter = UserAdapter(requireContext(), this) { show -> showMultiselectDelete(show) }

        setupRecyclerView()
        setListeners()
        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
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
        // Not used
    }

    override fun setListeners() {
        setupBackArrowListeners()
        setupAddContactListeners()
        setupMultiselectDeleteListeners()
    }

    private fun setupBackArrowListeners() {
        binding.toolbarBack.setOnClickListener {
            moveToFirstTab()
        }
    }

    private fun moveToFirstTab() {
        val viewPager = activity?.findViewById<ViewPager2>(R.id.viewPager)
        viewPager?.currentItem = Constants.FIRST_TAB_NUMBER
    }

    private fun isFirstTab() = viewPager.currentItem == Constants.FIRST_TAB_NUMBER

    private fun setupAddContactListeners() {
        val addContactsView = binding.addContactsLabel

        addContactsView.setOnClickListener {
            AddContactDialogFragment().show(
                childFragmentManager, AddContactDialogFragment.TAG
            )
        }

        childFragmentManager.setFragmentResultListener(
            Constants.CONTACT_INFO_KEY, this) { _, bundle ->
            val user = User(
                User.generateId(),
                Constants.DEFAULT_USER_IMAGE_PATH,
                bundle.getString(Constants.CONTACT_NAME_KEY)!!,
                bundle.getString(Constants.CONTACT_CAREER_KEY)!!,
                bundle.getString(Constants.CONTACT_ADDRESS_KEY)!!,
                false
            )

            viewModel.addUser(0, user)

            adapter.submitList(viewModel.users.value)
        }
    }

    override fun onContactItemClicked(user: User) {
        val extras = FragmentNavigatorExtras(binding.contactsBackground to "detailBackground")

        findNavController().navigate(
            ViewPagerFragmentDirections.actionViewPagerFragmentToDetailViewFragment(
                user
            ), extras
        )
    }

    override fun onDeleteItemClicked(user: User, position: Int) {
        viewModel.deleteUser(user.id)

        adapter.submitList(viewModel.users.value)

        showDeleteSnackbar(user, position)

        adapter.notifyItemRangeChanged(position, adapter.itemCount)
    }

    private fun showDeleteSnackbar(user: User, position: Int) {

        val snackbar = Snackbar.make(binding.root,
            getString(R.string.contact_removed), Snackbar.LENGTH_LONG)

        snackbar.setAction(getString(R.string.undo_button)) {
            viewModel.addUser(position, user)
        }

        snackbar.show()
    }

    private fun setupRecyclerView() {
        val contactsRV = binding.rvContacts

        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        setupAdapterScroll(contactsRV)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(requireContext())

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback { position, user ->
            viewModel.deleteUser(user.id)

            showDeleteSnackbar(user, position)
        })

        itemTouchHelper.attachToRecyclerView(contactsRV)

        viewModel.init()

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

    private fun setupAnimation() {

        animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )

        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    private fun setupMultiselectDeleteListeners() {
        val multiselectDeleteIcon = binding.ivMultiselectDelete
        multiselectDeleteIcon.setOnClickListener {

            val selectedItems = adapter.getSelectedItems().sortedDescending()

            selectedItems.forEach {
                viewModel.deleteUser(viewModel.users.value[it].id)
            }

            adapter.exitMultiselectMode()
        }
    }

    private fun showMultiselectDelete(show: Boolean) {
        binding.ivMultiselectDelete.isVisible = show
    }
}