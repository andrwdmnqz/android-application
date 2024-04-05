package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MyContactsActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val adapter = UserAdapter(this, emptyList())

        setupRecyclerView(adapter)

        setupAddContacts(viewModel)
    }

    private fun setupAddContacts(viewModel: UserViewModel) {
        val addContactsView = viewBinding.addContactsLabel

        addContactsView.setOnClickListener {
            AddContactDialogFragment(viewModel).show(
                supportFragmentManager, AddContactDialogFragment.TAG
            )
        }
    }

    override fun onDeleteItemClicked(user: User, position: Int) {
        showDeleteSnackbar(user, position)
    }

    private fun showDeleteSnackbar(user: User, position: Int) {

        viewModel.deleteUser(user)

        val snackbar = Snackbar.make(viewBinding.root,
            getString(R.string.contact_removed), Snackbar.LENGTH_LONG)

        snackbar.setAction(getString(R.string.undo_button)) {
            viewModel.addUser(position, user)
            viewBinding.rvContacts.scrollToPosition(position)
        }
        snackbar.show()
    }

    private fun setupRecyclerView(adapter: UserAdapter) {
        val contactsRV = viewBinding.rvContacts
        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(this)

        val itemTouchHelper = ItemTouchHelper(SwipeToDeleteCallback(viewModel, contactsRV))
        itemTouchHelper.attachToRecyclerView(contactsRV)

        viewModel.init()

        lifecycleScope.launch {
            viewModel.users.collect { users ->
                adapter.contactsList = users
                adapter.notifyDataSetChanged()
            }
        }
    }
}