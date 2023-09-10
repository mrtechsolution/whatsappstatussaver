package com.mrtech.whatsappstatussaver.easyvideoplayer

import android.net.Uri
import androidx.annotation.CheckResult
import androidx.annotation.IntRange
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoCallback
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoProgressCallback
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoPlayer.LeftAction
import com.mrtech.whatsappstatussaver.easyvideoplayer.EasyVideoPlayer.RightAction

/**
 * @author Aidan Follestad (afollestad)
 */
internal interface IUserMethods {
    fun setSource(source: Uri)
    fun setCallback(callback: EasyVideoCallback)
    fun setProgressCallback(callback: EasyVideoProgressCallback)
    fun setLeftAction(@LeftAction action: Int)
    fun setRightAction(@RightAction action: Int)
    fun setHideControlsOnPlay(hide: Boolean)
    fun setAutoPlay(autoPlay: Boolean)
    fun setInitialPosition(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int)
    fun showControls()
    fun hideControls()

    fun isControlsShown(): Boolean
    fun toggleControls()
    fun enableControls(andShow: Boolean)
    fun disableControls()

    fun isPrepared(): Boolean

    fun isPlaying(): Boolean

    fun currentPosition(): Int

    fun duration(): Int
    fun start()
    fun seekTo(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int)
    fun pause()
    fun stop()
    fun reset()
    fun release()
}