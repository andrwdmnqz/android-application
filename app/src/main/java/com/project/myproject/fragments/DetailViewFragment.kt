package com.project.myproject.fragments

import android.os.Bundle
import android.transition.TransitionInflater
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.project.myproject.R
import com.project.myproject.databinding.FragmentDetailViewBinding
import com.project.myproject.extensions.loadImageByGlide

class DetailViewFragment : Fragment(R.layout.fragment_detail_view) {

    private var _binding: FragmentDetailViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailViewBinding.inflate(layoutInflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupBackArrowListeners()

        setupTextFields()

        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    private fun setupAnimation() {
        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    private fun setupBackArrowListeners() {

        binding.toolbarBack.setOnClickListener {
            it.findNavController().popBackStack()
        }
    }

    private fun setupTextFields() {

        val bundle = DetailViewFragmentArgs.fromBundle(requireArguments())

        binding.profileImage.loadImageByGlide(bundle.imageLink)
        binding.tvName.text = bundle.name
        binding.tvCareer.text = bundle.career
        binding.tvAddress.text = bundle.address
    }
}