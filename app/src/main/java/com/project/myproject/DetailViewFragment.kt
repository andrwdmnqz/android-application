package com.project.myproject

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.project.myproject.databinding.FragmentDetailViewBinding
import kotlinx.coroutines.launch

class DetailViewFragment : Fragment(R.layout.fragment_detail_view) {

    private var _binding: FragmentDetailViewBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailViewBinding.inflate(layoutInflater)

        settingPreference = SettingPreference(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initializeLogoutButton()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initializeLogoutButton() {

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                settingPreference.clearData()

                it.findNavController().navigate(R.id.action_detailViewFragment_to_registerFragment)
            }
        }
    }

    companion object {
        const val FRAGMENT_TAG = "DetailViewFragment"
        fun newInstance(): DetailViewFragment = DetailViewFragment()
    }
}