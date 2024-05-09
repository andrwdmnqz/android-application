package com.project.myproject.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.project.myproject.utils.Constants
import com.project.myproject.ui.adapters.ViewPagerAdapter
import com.project.myproject.databinding.FragmentViewPagerBinding

class ViewPagerFragment : BaseFragment<FragmentViewPagerBinding>(FragmentViewPagerBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        val fragmentList = arrayListOf<BaseFragment<*>>(MyProfileFragment(), ContactsFragment())

        val adapter = ViewPagerAdapter(fragmentList, requireActivity().supportFragmentManager, lifecycle)

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                Constants.FIRST_TAB_NUMBER -> Constants.FIRST_TAB_TEXT
                Constants.SECOND_TAB_NUMBER -> Constants.SECOND_TAB_TEXT
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
}
