package com.project.myproject.ui.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.project.myproject.R
import com.project.myproject.databinding.FragmentContactsBinding
import com.project.myproject.databinding.FragmentDetailViewBinding
import com.project.myproject.utils.extensions.loadImageByGlide

class DetailViewFragment : BaseFragment<FragmentDetailViewBinding>(FragmentDetailViewBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        setupBackActionListeners()

        setupTextFields()

        setupAnimation()

        super.onViewCreated(view, savedInstanceState)
    }

    override fun onStart() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().popBackStack()
        }

        super.onStart()
    }

    override fun setObservers() {
        TODO("Not yet implemented")
    }

    override fun setListeners() {
        TODO("Not yet implemented")
    }

    private fun setupBackActionListeners() {

        binding.toolbarBack.setOnClickListener {
            Log.d("DEBUG", "back")
            it.findNavController().popBackStack()
        }
    }

    private fun setupTextFields() {

        val contact = DetailViewFragmentArgs.fromBundle(requireArguments()).contact

        with (binding) {
            profileImage.loadImageByGlide(contact.photo)
            tvName.text = contact.name
            tvCareer.text = contact.career
            tvAddress.text = contact.address
        }
    }

    private fun hideAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            view.visibility = View.INVISIBLE
        }
        binding.detailBackground.visibility = View.VISIBLE
    }

    private fun fadeAllViewsExceptBackground() {
        val mainLayout: ConstraintLayout = binding.clMain

        for (i in 0 until mainLayout.childCount) {
            val view: View = mainLayout.getChildAt(i)

            if (view == binding.detailBackground) {
                continue
            }

            view.visibility = View.VISIBLE

            val fadeInAnimation = AnimationUtils.loadAnimation(context, androidx.appcompat.R.anim.abc_fade_in)
            view.startAnimation(fadeInAnimation)
        }
    }

    private fun setupAnimation() {

        val animation = TransitionInflater.from(requireContext()).inflateTransition(
            R.transition.change_bounds
        )
        sharedElementEnterTransition = animation

        animation.addListener(object : Transition.TransitionListener {
            override fun onTransitionStart(transition: Transition) {
                hideAllViewsExceptBackground()
            }

            override fun onTransitionEnd(transition: Transition) {
                fadeAllViewsExceptBackground()
            }

            override fun onTransitionCancel(transition: Transition) {
                // Transition canceled
            }

            override fun onTransitionPause(transition: Transition) {
                // Transition paused
            }

            override fun onTransitionResume(transition: Transition) {
                // Transition resumed
            }
        })
    }
}