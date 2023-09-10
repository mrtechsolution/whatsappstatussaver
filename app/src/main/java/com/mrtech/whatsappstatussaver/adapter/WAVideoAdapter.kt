package com.mrtech.whatsappstatussaver.adapter

import android.content.Context
import com.mrtech.whatsappstatussaver.model.WAImageModel
import androidx.recyclerview.widget.RecyclerView
import com.mrtech.whatsappstatussaver.recycler.VideoViewHolder
import android.util.SparseBooleanArray
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import com.mrtech.whatsappstatussaver.R
import java.util.ArrayList

/**
 * Created by SONU on 27/03/16.
 */
class WAVideoAdapter(
    private val context: Context,

    private val arrayList: ArrayList<WAImageModel>?
) : RecyclerView.Adapter<VideoViewHolder>() {
    //Return all selected ids
    var selectedIds: SparseBooleanArray
        private set

    override fun getItemCount(): Int {
        return arrayList?.size ?: 0
    }

    override fun onBindViewHolder(
        holder: VideoViewHolder,
        position: Int
    ) {

        //Setting text over text view
        val centerCrop =
            RequestOptions().override(holder.imageView.width, holder.imageView.height).centerCrop()
        Glide.with(context).asBitmap().apply(centerCrop).load(arrayList!![position].path as String)
            .transition(BitmapTransitionOptions.withCrossFade()).into(holder.imageView)
        if (selectedIds[position]) {
            holder.imageViewCheck.visibility = View.VISIBLE
            holder.imageViewPlay.visibility = View.GONE
        } else {
            holder.imageViewCheck.visibility = View.GONE
            holder.imageViewPlay.visibility = View.VISIBLE
        }
    }

    override fun onCreateViewHolder(
        viewGroup: ViewGroup, viewType: Int
    ): VideoViewHolder {
        val mInflater = LayoutInflater.from(viewGroup.context)
        val mainGroup = mInflater.inflate(
            R.layout.wa_video_list_item, viewGroup, false
        ) as ViewGroup
        return VideoViewHolder(mainGroup)
    }

    /***
     * Methods required for do selections, remove selections, etc.
     */
    //Toggle selection methods
    fun toggleSelection(position: Int) {
        selectView(position, !selectedIds[position])
    }

    //Remove selected selections
    fun removeSelection() {
        selectedIds = SparseBooleanArray()
        notifyDataSetChanged()
    }

    //Put or delete selected position into SparseBooleanArray
    fun selectView(position: Int, value: Boolean) {
        if (value) selectedIds.put(position, value) else selectedIds.delete(position)
        notifyDataSetChanged()
    }

    //Get total selected count
    val selectedCount: Int
        get() = selectedIds.size()

    fun getItem(i: Int): WAImageModel {
        return arrayList!![i]
    }

    fun updateData(viewModels: ArrayList<WAImageModel>?) {
        arrayList!!.clear()
        arrayList.addAll(viewModels!!)
        notifyDataSetChanged()
    }

    init {
        selectedIds = SparseBooleanArray()
    }
}