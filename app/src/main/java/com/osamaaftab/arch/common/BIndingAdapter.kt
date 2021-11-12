package com.osamaaftab.arch.common

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.aghajari.zoomhelper.ZoomHelper
import com.bumptech.glide.Priority
import com.bumptech.glide.request.RequestOptions
import com.osamaaftab.arch.R
import com.mikhaellopez.circularprogressbar.CircularProgressBar


@BindingAdapter("url", "progress")
fun ImageView.setImageUrl(url: String, progress: CircularProgressBar) {
    val options: RequestOptions = RequestOptions()
        .centerCrop()
        .error(R.drawable.ic_warning)
        .priority(Priority.HIGH)

    GlideImageLoader(this, progress).load(url, options)
    ZoomHelper.addZoomableView(this)
}