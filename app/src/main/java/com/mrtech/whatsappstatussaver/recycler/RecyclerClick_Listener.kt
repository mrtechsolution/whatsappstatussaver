package com.mrtech.whatsappstatussaver.recycler

import android.view.View

/**
 * Created by SONU on 15/03/16.
 */
interface RecyclerClick_Listener {
    /**
     * Interface for Recycler View Click listener
     */
    fun onClick(view: View?, position: Int)
    fun onLongClick(view: View?, position: Int)
}