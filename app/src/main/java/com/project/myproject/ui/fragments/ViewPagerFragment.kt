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
        super.onViewCreated(view, savedInstanceState)

        setupViewPager()
        setupTabLayout()
    }

    private fun setupViewPager() {
        val fragmentsList = listOf<BaseFragment<*>>(MyProfileFragment(), ContactsFragment())

        val adapter = ViewPagerAdapter(fragmentsList, childFragmentManager, lifecycle)
        binding.viewPager.adapter = adapter
    }

    private fun setupTabLayout() {
        initializeTabTitles(requireContext())

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = getTabTitle(position)
        }.attach()
    }

    private fun initializeTabTitles(context: Context) {
        firstTabText = context.getString(R.string.first_tab_text)
        secondTabText = context.getString(R.string.second_tab_text)
    }

    private fun getTabTitle(position: Int): String {
        return when (position) {
            Constants.FIRST_TAB_NUMBER -> firstTabText
            Constants.SECOND_TAB_NUMBER -> secondTabText
            else -> throw IllegalStateException("Invalid tab position $position")
        }
    }

    override fun setObservers() {
        // Not used
    }

    override fun setListeners() {
        // Not used
    }

    companion object {
        private var firstTabText = ""
        private var secondTabText = ""
    }
}
