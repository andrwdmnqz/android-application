package com.project.myproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.myproject.adapters.UserAdapter
import com.project.myproject.callbacks.SwipeToDeleteCallback
import com.project.myproject.databinding.MyContactsActivityBinding
import com.project.myproject.decorators.UserItemDecorator
import com.project.myproject.dialogs.AddContactDialogFragment
import com.project.myproject.extensions.loadImageByGlide
import com.project.myproject.models.User
import com.project.myproject.viewmodels.UserViewModel
import kotlinx.coroutines.launch

class MyContacts : AppCompatActivity() {
    private lateinit var userViewModel: UserViewModel
    private lateinit var viewBinding: MyContactsActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = MyContactsActivityBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val adapter = UserAdapter(ArrayList())

        setupAddContacts(adapter)

        setupRecyclerView(adapter)
    }

    private fun setupAddContacts(adapter: UserAdapter) {
        val addContactsView = viewBinding.addContactsLabel

        addContactsView.setOnClickListener {
            AddContactDialogFragment(adapter).show(
                supportFragmentManager, AddContactDialogFragment.TAG
            )
        }
    }

    private fun setupRecyclerView(adapter: UserAdapter) {
        val contactsRV = viewBinding.rvContacts
        val itemMarginSize = resources.getDimensionPixelSize(R.dimen.contacts_item_margin)

        contactsRV.adapter = adapter
        contactsRV.addItemDecoration(UserItemDecorator(itemMarginSize))
        contactsRV.layoutManager = LinearLayoutManager(this)

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