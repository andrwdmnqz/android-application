package com.project.myproject.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.android.material.tabs.TabLayoutMediator
import com.project.myproject.R
import com.project.myproject.utils.Constants
import com.project.myproject.ui.adapters.ViewPagerAdapter
import com.project.myproject.databinding.FragmentViewPagerBinding

class ViewPagerFragment : BaseFragment<FragmentViewPagerBinding>(FragmentViewPagerBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val fragmentList = arrayListOf<BaseFragment<*>>(MyProfileFragment(), ContactsFragment())

        val adapter = ViewPagerAdapter(fragmentList, childFragmentManager, lifecycle)

        viewPager.adapter = adapter

        initializeStrings(requireContext())

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                Constants.FIRST_TAB_NUMBER -> firstTabText
                Constants.SECOND_TAB_NUMBER -> secondTabText
                else -> throw IllegalStateException()
            }
        }.attach()
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        super.onDestroyView()
    }

    override fun setObservers() {
        // Not used
    }

    override fun setListeners() {
        // Not used
    }

    companion object {
        private var firstTabText = "My profile"
        private var secondTabText = "Contacts"

        fun initializeStrings(context: Context) {
            firstTabText = context.getString(R.string.first_tab_text)
            secondTabText = context.getString(R.string.second_tab_text)
        }
    }
}
