package com.mrtech.whatsappstatussaver.recycler

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mrtech.whatsappstatussaver.R

/**
 * Created by SONU on 27/03/16.
 */
class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView: ImageView
    var imageViewCheck: ImageView

    init {
        imageView = view.findViewById<View>(R.id.imageView_wa_image) as ImageView
        imageViewCheck = view.findViewById<View>(R.id.imageView_wa_checked) as ImageView
    }
}