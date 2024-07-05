package com.project.myproject.ui.fragments.contacts_profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.data.room.entities.Contact
import com.project.myproject.domain.Repository
import com.project.myproject.utils.NetworkUtil
import com.project.myproject.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ContactsProfileViewModel @Inject constructor(
    private val repository: Repository,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contactsId = MutableStateFlow<List<Int>>(emptyList())
    val contactsId = _contactsId.asStateFlow()

    private val _contactAdded = MutableStateFlow(false)
    val contactAdded = _contactAdded.asStateFlow()

    fun addContact(contactId: Int) {

        viewModelScope.launch(Dispatchers.IO) {
            if (networkUtil.isInternetAvailable()) {

                val result = repository.addContact(sessionManager.getId(), AddContactRequest(contactId))

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        _contacts.value = result.data.contacts.map { user ->
                            user.toContact()
                        }
                        _contactsId.value = result.data.contacts.map { it.id }
                        _contactAdded.value = true
                    }
                }
            }
        }
    }

    fun resetContactAdded() {
        _contactAdded.value = false
    }
}