package com.mrtech.whatsappstatussaver.easyvideoplayer

import android.net.Uri
import java.lang.Exception

/**
 * @author Aidan Follestad (afollestad)
 */
interface EasyVideoCallback {
    fun onPreparing(player: EasyVideoPlayer?)
    fun onPrepared(player: EasyVideoPlayer?)
    fun onBuffering(percent: Int)
    fun onError(player: EasyVideoPlayer?, e: Exception?)
    fun onCompletion(player: EasyVideoPlayer?)
    fun onRetry(player: EasyVideoPlayer?, source: Uri?)
    fun onSubmit(player: EasyVideoPlayer?, source: Uri?)
}