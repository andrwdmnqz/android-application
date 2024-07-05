package com.project.myproject.ui.fragments.search_contacts.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.myproject.data.repository.ContactRepository
import com.project.myproject.data.requests.AddContactRequest
import com.project.myproject.data.room.entities.Contact
import com.project.myproject.domain.Repository
import com.project.myproject.utils.NetworkUtil
import com.project.myproject.utils.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class SearchContactsViewModel @Inject constructor(
    private val repository: Repository,
    private val networkUtil: NetworkUtil,
    private val sessionManager: SessionManager,
    private val contactRepository: ContactRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _contacts = MutableStateFlow<List<Contact>>(emptyList())
    val contacts = _contacts.asStateFlow()

    private val _contactsId = MutableStateFlow<List<Int>>(emptyList())
    val contactsId = _contactsId.asStateFlow()

    private val _contactAdded = MutableStateFlow(false)
    val contactAdded = _contactAdded.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, _ ->
        onError()
    }

    fun fetchContacts() {
        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            if (networkUtil.isInternetAvailable()) {

                _loading.value = true

                val result = repository.getUserContacts(sessionManager.getId())

                withContext(Dispatchers.Main) {
                    _loading.value = false
                    if (result != null) {
                        _contacts.value = result.data.contacts.map { user ->
                            user.toContact()
                        }
                        _contactsId.value = result.data.contacts.map { it.id }
                        contactRepository.replaceContacts(_contacts.value)
                    }
                }
            } else {
                _loading.value = true
                withContext(Dispatchers.Main) {
                    _contacts.value = contactRepository.getAllContacts()
                    _loading.value = false
                }
            }
        }
    }

    fun deleteContact(contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            if (networkUtil.isInternetAvailable()) {
                val result = repository.deleteUserContact(sessionManager.getId(), contactId)

                withContext(Dispatchers.Main) {
                    if (result != null) {
                        _contacts.value = result.data.contacts.map { user ->
                            user.toContact()
                        }
                        _contactsId.value = result.data.contacts.map { it.id }
                    }
                }
            }
        }
    }

    fun addContact(contactId: Int) {

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            if (networkUtil.isInternetAvailable()) {
                _loading.value = true

                val result = repository.addContact(sessionManager.getId(), AddContactRequest(contactId))

                withContext(Dispatchers.Main) {
                    _loading.value = false
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

    private fun onError() {
        _loading.value = false
    }
}