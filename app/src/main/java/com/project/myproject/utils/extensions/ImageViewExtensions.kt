package com.project.myproject.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageByGlide(url: String) {
    Glide.with(this)
        .load(url)
        .circleCrop()
        .into(this)
}