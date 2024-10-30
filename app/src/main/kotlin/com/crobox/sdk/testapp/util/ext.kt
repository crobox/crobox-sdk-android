package com.crobox.sdk.testapp.util

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.crobox.sdk.testapp.R

fun ImageView.loadImage(imageUrl: String) {
    if (imageUrl.isNotEmpty()) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.loading_spinner)
            .into(this)
    } else {
        setImageResource(R.mipmap.ic_launcher)
    }
}