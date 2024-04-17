package com.project.myproject.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.project.myproject.R
import com.project.myproject.adapters.ViewPagerAdapter
import com.project.myproject.databinding.FragmentViewPagerBinding


private const val FIRST_TAB_NUMBER = 0
private const val SECOND_TAB_NUMBER = 1
private const val FIRST_TAB_TEXT = "My profile"
private const val SECOND_TAB_TEXT = "Contacts"

class ViewPagerFragment : Fragment(R.layout.fragment_view_pager) {

    private var _binding: FragmentViewPagerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(layoutInflater, container, false)

        val fragmentList = arrayListOf(
            MyProfileFragment(),
            ContactsFragment()
        )

        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        val viewPager = binding.viewPager
        val tabLayout = binding.tabLayout

        viewPager.adapter = adapter

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = when (position) {
                FIRST_TAB_NUMBER -> FIRST_TAB_TEXT
                SECOND_TAB_NUMBER -> SECOND_TAB_TEXT
                else -> throw IllegalStateException()
            }
        }.attach()


        return binding.root
    }
}