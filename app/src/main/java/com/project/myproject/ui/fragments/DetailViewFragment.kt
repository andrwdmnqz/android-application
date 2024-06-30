package com.project.myproject.ui.fragments

import android.os.Bundle
import android.transition.Transition
import android.transition.TransitionInflater
import android.view.View
import android.view.animation.AnimationUtils
import androidx.activity.addCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.project.myproject.R
import com.project.myproject.databinding.FragmentDetailViewBinding
import com.project.myproject.utils.Constants
import com.project.myproject.utils.extensions.loadImageByGlide

class DetailViewFragment : BaseFragment<FragmentDetailViewBinding>(FragmentDetailViewBinding::inflate) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setObservers()
        setupAnimation()
    }

    override fun onStart() {
        super.onStart()
        handleOnBackPressed()
    }

    private fun handleOnBackPressed() {
        requireActivity().onBackPressedDispatcher.addCallback(this) {
            findNavController().navigateUp()
        }
    }

    override fun setObservers() {
        setupFields()
    }

    override fun setListeners() {
        setupBackActionListeners()
    }

    private fun setupBackActionListeners() {
        binding.ivToolbarBack.setOnClickListener {
            it.findNavController().navigateUp()
        }
    }

    private fun setupFields() {

        val contact = DetailViewFragmentArgs.fromBundle(requireArguments()).contact

        with (binding) {
            val image = contact.image

            if (image != null) {
                ivContactProfileImage.loadImageByGlide(image)
            } else {
                ivContactProfileImage.setImageResource(R.mipmap.empty_photo_icon)
            }

            tvName.text = contact.name.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_NAME_VALUE
            tvCareer.text = contact.career.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_CAREER_VALUE
            tvAddress.text = contact.address.takeUnless { it.isNullOrBlank() } ?: Constants.DEFAULT_ADDRESS_VALUE
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