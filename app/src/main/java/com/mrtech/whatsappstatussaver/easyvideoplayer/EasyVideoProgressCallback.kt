package com.mrtech.whatsappstatussaver.easyvideoplayer

/**
 * @author Aidan Follestad (afollestad)
 */
interface EasyVideoProgressCallback {
    fun onVideoProgressUpdate(position: Int, duration: Int)
}