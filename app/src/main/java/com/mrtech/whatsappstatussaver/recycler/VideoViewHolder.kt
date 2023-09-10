package com.mrtech.whatsappstatussaver.recycler

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.mrtech.whatsappstatussaver.R

/**
 * Created by umer on 01-May-18.
 */
class VideoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    var imageView: ImageView
    var imageViewCheck: ImageView
    var imageViewPlay: ImageView

    init {
        imageView = view.findViewById<View>(R.id.imageView_wa_image) as ImageView
        imageViewCheck = view.findViewById<View>(R.id.imageView_wa_checked) as ImageView
        imageViewPlay = view.findViewById<View>(R.id.imageView_wa_play) as ImageView
    }
}