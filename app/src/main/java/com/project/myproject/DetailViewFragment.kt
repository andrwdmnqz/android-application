package com.project.myproject

import android.os.Bundle
import android.transition.TransitionInflater
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

        parseName()

        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAnimation() {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            android.R.transition.move
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
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

    private fun parseName() {
        var name = DetailViewFragmentArgs.fromBundle(requireArguments()).email
        val nameField = binding.name

        name = name.substringBefore('@')

        val splittedName = name.split('.')
            .map { it.replaceFirstChar { char -> char.uppercaseChar() } }

        val nameText: String

        if (splittedName.size > 1) {
            nameText = "${splittedName[0]} ${splittedName[1]}"
        } else {
            nameText = getString(R.string.name_placeholder, splittedName[0])
        }
        nameField.text = nameText
    }
}