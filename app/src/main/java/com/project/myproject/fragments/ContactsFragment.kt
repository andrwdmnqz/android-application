package com.project.myproject.fragments

import android.os.Bundle
import android.os.Handler
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.Constants
import com.project.myproject.R
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.FragmentContactsBinding
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.dialogs.AddContactDialogFragment
import com.project.myproject.models.User
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class ContactsFragment : Fragment(R.layout.fragment_contacts), UserAdapter.OnUserItemClickListener {
    private val viewModel: UserViewModel by viewModels<UserViewModel>()

    private var _binding: FragmentContactsBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: UserAdapter

    private lateinit var animation: Transition

    private lateinit var viewPager: ViewPager2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentContactsBinding.inflate(layoutInflater, container, false)

        viewPager = activity?.findViewById(R.id.viewPager)!!

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        adapter = UserAdapter(requireContext(), this) { show -> showMultiselectDelete(show) }

        setupRecyclerView()
        setupBackArrowListeners()
        setupAddContactListeners()
        setupAnimation()
        setupMultiselectDeleteListeners()

        super.onViewCreated(view, savedInstanceState)
    }



    override fun onStart() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {

            if (isFirstTab()) {
                activity?.finish()
            } else {
                moveToFirstTab()
            }
        }

        super.onStart()
    }

    private fun setupBackArrowListeners() {

        binding.toolbarBack.setOnClickListener {
//            it.findNavController().popBackStack()
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
                user.photo, user.name,
                user.career, user.address
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

            adapter.submitList(viewModel.users.value)
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

    private fun hideAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            view.visibility = View.INVISIBLE
        }
        binding.contactsBackground.visibility = View.VISIBLE
    }

    private fun fadeAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            if (view == binding.contactsBackground) {
                continue
            }

            view.visibility = View.VISIBLE

            val fadeInAnimation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
            view.startAnimation(fadeInAnimation)
        }
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

            multiselectDeleteIcon.visibility = View.INVISIBLE
        }
    }

    private fun showMultiselectDelete(show: Boolean) {
        binding.ivMultiselectDelete.isVisible = show
    }
}