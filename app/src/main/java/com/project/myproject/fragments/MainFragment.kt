package com.project.myproject.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.project.myproject.R
import com.project.myproject.SettingPreference
import com.project.myproject.databinding.FragmentMainBinding
import kotlinx.coroutines.launch

class MainFragment : Fragment(R.layout.fragment_main) {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var settingPreference: SettingPreference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)

        settingPreference = SettingPreference(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        initializeLogoutButtonListeners()

        initializeContactsButtonListeners()

        parseName()

        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAnimation() {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
        sharedElementReturnTransition = animation
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun initializeLogoutButtonListeners() {

        binding.logoutButton.setOnClickListener {
            lifecycleScope.launch {
                settingPreference.clearData()

                val extras = FragmentNavigatorExtras(binding.topRectangle to "registerBackground")
                it.findNavController().navigate(
                    MainFragmentDirections.actionMainFragmentToRegisterFragment(), extras)
            }
        }
    }

    private fun initializeContactsButtonListeners() {

        binding.contactsButton.setOnClickListener {

            it.findNavController().navigate(R.id.action_mainFragment_to_contactsFragment)
        }
    }

    private fun parseName() {
        var name = MainFragmentArgs.fromBundle(requireArguments()).email
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