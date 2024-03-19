package com.project.myproject.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide

fun ImageView.loadImageByGlide(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}