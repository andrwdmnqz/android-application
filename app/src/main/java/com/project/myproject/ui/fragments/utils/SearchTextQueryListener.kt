package com.project.myproject.ui.fragments.utils

import androidx.appcompat.widget.SearchView

class SearchTextQueryListener(
    private val filter: (String) -> Unit
) : SearchView.OnQueryTextListener {

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        if (newText != null) {
            filter(newText)
        }
        return true
    }
}