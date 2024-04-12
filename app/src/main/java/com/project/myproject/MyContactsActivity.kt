package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.MyContactsActivityBinding
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.dialogs.AddContactDialogFragment
import com.project.myproject.models.User
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MyContactsActivity : AppCompatActivity(), UserAdapter.OnDeleteItemClickListener {
    private val viewModel: UserViewModel by viewModels<UserViewModel>()
    private lateinit var viewBinding: MyContactsActivityBinding
    private lateinit var adapter: UserAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MyContactsActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        adapter = UserAdapter(this)

        setupRecyclerView()

        setupAddContactListeners()
    }

    private fun setupAddContactListeners() {
        val addContactsView = viewBinding.addContactsLabel

        addContactsView.setOnClickListener {
            AddContactDialogFragment().show(
                supportFragmentManager, AddContactDialogFragment.TAG
            )
        }

        supportFragmentManager.setFragmentResultListener(
            Constants.CONTACT_INFO_KEY, this) { _, bundle ->
            val user = User(
                User.generateId(),
                Constants.DEFAULT_USER_IMAGE_PATH,
                bundle.getString(Constants.CONTACT_NAME_KEY)!!,
                bundle.getString(Constants.CONTACT_CAREER_KEY)!!,
                bundle.getString(Constants.CONTACT_ADDRESS_KEY)!!
            )

            viewModel.addUser(0, user)

            adapter.submitList(viewModel.users.value)
        }
    }

    override fun onDeleteItemClicked(user: User, position: Int) {
        viewModel.deleteUser(user.id)

        showDeleteSnackbar(user, position)
    }

    private fun showDeleteSnackbar(user: User, position: Int) {

        val snackbar = Snackbar.make(viewBinding.root,
            getString(R.string.contact_removed), Snackbar.LENGTH_LONG)

        snackbar.setAction(getString(R.string.undo_button)) {
            viewModel.addUser(position, user)

            adapter.submitList(viewModel.users.value)
        }
        snackbar.show()
    }

    private fun setupRecyclerView() {
        val contactsRV = viewBinding.rvContacts

        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        setupAdapterScroll(contactsRV)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(this)

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
                if (itemCount == 1) {
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
}