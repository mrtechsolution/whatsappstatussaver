package com.mrtech.whatsappstatussaver.easyvideoplayer

import android.animation.Animator
import android.view.TextureView.SurfaceTextureListener
import android.media.MediaPlayer.OnPreparedListener
import android.media.MediaPlayer.OnBufferingUpdateListener
import android.media.MediaPlayer.OnCompletionListener
import android.media.MediaPlayer.OnVideoSizeChangedListener
import android.media.MediaPlayer
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.annotation.IntDef
import android.animation.AnimatorListenerAdapter
import android.content.Context
import android.graphics.Color
import android.graphics.Matrix
import com.mrtech.whatsappstatussaver.R
import android.graphics.SurfaceTexture
import android.media.AudioManager
import android.net.Uri
import android.os.Handler
import android.util.AttributeSet
import android.util.Log
import android.view.*
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.annotation.CheckResult
import androidx.annotation.IntRange
import java.io.IOException
import java.lang.Exception
import java.lang.RuntimeException
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * @author Aidan Follestad (afollestad)
 */
class EasyVideoPlayer : FrameLayout, IUserMethods, SurfaceTextureListener, OnPreparedListener,
    OnBufferingUpdateListener, OnCompletionListener, OnVideoSizeChangedListener,
    MediaPlayer.OnErrorListener, View.OnClickListener, OnSeekBarChangeListener {
    @IntDef(LEFT_ACTION_NONE, LEFT_ACTION_RESTART, LEFT_ACTION_RETRY)
    @Retention(RetentionPolicy.SOURCE)
    annotation class LeftAction

    @IntDef(RIGHT_ACTION_NONE, RIGHT_ACTION_SUBMIT)
    @Retention(RetentionPolicy.SOURCE)
    annotation class RightAction {}

    constructor(context: Context?) : super(context!!) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context!!, attrs, defStyleAttr
    ) {
        init()
    }

    private var mTextureView: TextureView? = null
    private var mSurface: Surface? = null
    private var mControlsFrame: View? = null
    private var mProgressFrame: View? = null
    private var mClickFrame: View? = null
    private var mSeeker: SeekBar? = null
    private var mLabelPosition: TextView? = null
    private var mLabelDuration: TextView? = null
    private var mBtnRestart: ImageButton? = null
    private var mBtnRetry: Button? = null
    private var mBtnPlayPause: ImageButton? = null
    private var mBtnSubmit: Button? = null
    private var mPlayer: MediaPlayer? = null
    private var mSurfaceAvailable = false
    private var mIsPrepared = false
    private var mWasPlaying = false
    private var mInitialTextureWidth = 0
    private var mInitialTextureHeight = 0
    private var mHandler: Handler? = null
    private var mSource: Uri? = null
    private var mCallback: EasyVideoCallback? = null
    private var mProgressCallback: EasyVideoProgressCallback? = null

    @LeftAction
    private var mLeftAction = LEFT_ACTION_RESTART

    @RightAction
    private var mRightAction = RIGHT_ACTION_NONE
    private var mHideControlsOnPlay = true
    private var mAutoPlay = false
    private var mInitialPosition = -1
    private var mControlsDisabled = false

    // Runnable used to run code on an interval to update counters and seeker
    private val mUpdateCounters: Runnable = object : Runnable {
        override fun run() {
            if (mHandler == null || !mIsPrepared || mSeeker == null || mPlayer == null) return
            val pos = mPlayer!!.currentPosition
            val dur = mPlayer!!.duration
            mLabelPosition!!.text = Util.getDurationString(pos.toLong(), false)
            mLabelDuration!!.text = Util.getDurationString((dur - pos).toLong(), true)
            mSeeker!!.progress = pos
            if (mProgressCallback != null) mProgressCallback!!.onVideoProgressUpdate(pos, dur)
            if (mHandler != null) mHandler!!.postDelayed(this, UPDATE_INTERVAL.toLong())
        }
    }

    private fun init() {
        setBackgroundColor(Color.BLACK)
    }

    override fun setSource(source: Uri) {
        mSource = source
        if (mPlayer != null) prepare()
    }

    override fun setCallback(callback: EasyVideoCallback) {
        mCallback = callback
    }

    override fun setProgressCallback(callback: EasyVideoProgressCallback) {
        mProgressCallback = callback
    }

    override fun setLeftAction(@LeftAction action: Int) {
        require(!(action < LEFT_ACTION_NONE || action > LEFT_ACTION_RETRY)) { "Invalid left action specified." }
        mLeftAction = action
        invalidateActions()
    }

    override fun setRightAction(@RightAction action: Int) {
        require(!(action < RIGHT_ACTION_NONE || action > RIGHT_ACTION_SUBMIT)) { "Invalid right action specified." }
        mRightAction = action
        invalidateActions()
    }

    override fun setHideControlsOnPlay(hide: Boolean) {
        mHideControlsOnPlay = hide
    }

    override fun setAutoPlay(autoPlay: Boolean) {
        mAutoPlay = autoPlay
    }

    override fun setInitialPosition(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int) {
        mInitialPosition = pos
    }

    private fun prepare() {
        if (!mSurfaceAvailable || mSource == null || mPlayer == null || mIsPrepared) return
        try {
            if (mCallback != null) mCallback!!.onPreparing(this)
            mPlayer!!.setSurface(mSurface)
            if (mSource!!.scheme == "http" || mSource!!.scheme == "https") {
                LOG("Loading web URI: " + mSource.toString())
                mPlayer!!.setDataSource(mSource.toString())
            } else {
                LOG("Loading local URI: " + mSource.toString())
                mPlayer!!.setDataSource(context, mSource!!)
            }
            mPlayer!!.prepareAsync()
        } catch (e: IOException) {
            throwError(e)
        }
    }

    private fun setControlsEnabled(enabled: Boolean) {
        if (mSeeker == null) return
        mSeeker!!.isEnabled = enabled
        mBtnPlayPause!!.isEnabled = enabled
        mBtnSubmit!!.isEnabled = enabled
        mBtnRestart!!.isEnabled = enabled
        mBtnRetry!!.isEnabled = false
        val disabledAlpha = .4f
        mBtnPlayPause!!.alpha = if (enabled) 1f else disabledAlpha
        mBtnSubmit!!.alpha = if (enabled) 1f else disabledAlpha
        mBtnRestart!!.alpha = if (enabled) 1f else disabledAlpha
        mClickFrame!!.isEnabled = enabled
    }

    override fun showControls() {
        if (mControlsDisabled || isControlsShown() || mSeeker == null) return
        mControlsFrame!!.animate().cancel()
        mControlsFrame!!.alpha = 0f
        mControlsFrame!!.visibility = VISIBLE
        mControlsFrame!!.animate().alpha(1f).setListener(null)
            .setInterpolator(DecelerateInterpolator()).start()
    }

    override fun hideControls() {
        if (mControlsDisabled || !isControlsShown() || mSeeker == null) return
        mControlsFrame!!.animate().cancel()
        mControlsFrame!!.alpha = 1f
        mControlsFrame!!.visibility = VISIBLE
        mControlsFrame!!.animate().alpha(0f)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    if (mControlsFrame != null) mControlsFrame!!.visibility = GONE
                }
            }).start()
    }

    override fun isControlsShown(): Boolean {
        return !mControlsDisabled && mControlsFrame != null && mControlsFrame!!.alpha > .5f
    }

    override fun toggleControls() {
        if (mControlsDisabled) return
        if (isControlsShown()) {
            hideControls()
        } else {
            showControls()
        }
    }

    override fun enableControls(andShow: Boolean) {
        mControlsDisabled = false
        if (andShow) showControls()
    }

    override fun disableControls() {
        mControlsDisabled = true
        mControlsFrame!!.visibility = GONE
    }

    @CheckResult
    override fun isPrepared(): Boolean {
        return mPlayer != null && mIsPrepared
    }

    @CheckResult
    override fun isPlaying(): Boolean {
        return mPlayer != null && mPlayer!!.isPlaying
    }

    override fun currentPosition(): Int {
        return if (mPlayer == null) -1 else mPlayer!!.currentPosition
    }

    override fun duration(): Int {
        return if (mPlayer == null) -1 else mPlayer!!.duration
    }

    override fun start() {
        if (mPlayer == null) return
        mPlayer!!.start()
        if (mHandler == null) mHandler = Handler()
        mHandler!!.post(mUpdateCounters)
        mBtnPlayPause!!.setImageResource(R.drawable.evp_action_pause)
    }

    override fun seekTo(@IntRange(from = 0, to = Int.MAX_VALUE.toLong()) pos: Int) {
        if (mPlayer == null) return
        mPlayer!!.seekTo(pos)
    }

    override fun pause() {
        if (mPlayer == null || !isPlaying()) return
        mPlayer!!.pause()
        if (mHandler == null) return
        mHandler!!.removeCallbacks(mUpdateCounters)
        mBtnPlayPause!!.setImageResource(R.drawable.evp_action_play)
    }

    override fun stop() {
        if (mPlayer == null) return
        try {
            mPlayer!!.stop()
        } catch (ignored: Throwable) {
        }
        if (mHandler == null) return
        mHandler!!.removeCallbacks(mUpdateCounters)
        mBtnPlayPause!!.setImageResource(R.drawable.evp_action_pause)
    }

    override fun reset() {
        if (mPlayer == null) return
        mIsPrepared = false
        mPlayer!!.reset()
        mIsPrepared = false
    }

    override fun release() {
        if (mPlayer == null) return
        mIsPrepared = false
        try {
            mPlayer!!.release()
        } catch (ignored: Throwable) {
        }
        mPlayer = null
        if (mHandler != null) {
            mHandler!!.removeCallbacks(mUpdateCounters)
            mHandler = null
        }
        LOG("Released player and Handler")
    }

    // Surface listeners
    override fun onSurfaceTextureAvailable(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        LOG("Surface texture available: %dx%d", width, height)
        mInitialTextureWidth = width
        mInitialTextureHeight = height
        mSurfaceAvailable = true
        mSurface = Surface(surfaceTexture)
        if (mIsPrepared) {
            mPlayer!!.setSurface(mSurface)
        } else {
            prepare()
        }
    }

    override fun onSurfaceTextureSizeChanged(
        surfaceTexture: SurfaceTexture,
        width: Int,
        height: Int
    ) {
        LOG("Surface texture changed: %dx%d", width, height)
        adjustAspectRatio(width, height, mPlayer!!.videoWidth, mPlayer!!.videoHeight)
    }

    override fun onSurfaceTextureDestroyed(surfaceTexture: SurfaceTexture): Boolean {
        LOG("Surface texture destroyed")
        mSurfaceAvailable = false
        mSurface = null
        return false
    }

    override fun onSurfaceTextureUpdated(surfaceTexture: SurfaceTexture) {}

    // Media player listeners
    override fun onPrepared(mediaPlayer: MediaPlayer) {
        LOG("onPrepared()")
        mProgressFrame!!.visibility = INVISIBLE
        mIsPrepared = true
        if (mCallback != null) mCallback!!.onPrepared(this)
        mLabelPosition!!.text = Util.getDurationString(0, false)
        mLabelDuration!!.text = Util.getDurationString(mediaPlayer.duration.toLong(), false)
        mSeeker!!.progress = 0
        mSeeker!!.max = mediaPlayer.duration
        setControlsEnabled(true)
        if (mAutoPlay) {
            start()
            if (mInitialPosition > 0) {
                seekTo(mInitialPosition)
                mInitialPosition = -1
            }
        } else {
            // Hack to show first frame, is there another way?
            mPlayer!!.start()
            mPlayer!!.pause()
        }
    }

    override fun onBufferingUpdate(mediaPlayer: MediaPlayer, percent: Int) {
        LOG("Buffering: %d%%", percent)
        if (mCallback != null) mCallback!!.onBuffering(percent)
        if (mSeeker != null) {
            if (percent == 100) mSeeker!!.secondaryProgress = 0 else mSeeker!!.secondaryProgress =
                mSeeker!!.max * (percent / 100)
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer) {
        LOG("onCompletion()")
        if (mCallback != null) mCallback!!.onCompletion(this)
        mBtnPlayPause!!.setImageResource(R.drawable.evp_action_play)
        if (mHandler != null) mHandler!!.removeCallbacks(mUpdateCounters)
        showControls()
    }

    override fun onVideoSizeChanged(mediaPlayer: MediaPlayer, width: Int, height: Int) {
        LOG("Video size changed: %dx%d", width, height)
        adjustAspectRatio(mInitialTextureWidth, mInitialTextureHeight, width, height)
    }

    override fun onError(mediaPlayer: MediaPlayer, what: Int, extra: Int): Boolean {
        if (what == -38) {
            // Error code -38 happens on some Samsung devices
            // Just ignore it
            return false
        }
        var errorMsg = "Preparation/playback error ($what): "
        errorMsg += when (what) {
            MediaPlayer.MEDIA_ERROR_IO -> "I/O error"
            MediaPlayer.MEDIA_ERROR_MALFORMED -> "Malformed"
            MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK -> "Not valid for progressive playback"
            MediaPlayer.MEDIA_ERROR_SERVER_DIED -> "Server died"
            MediaPlayer.MEDIA_ERROR_TIMED_OUT -> "Timed out"
            MediaPlayer.MEDIA_ERROR_UNSUPPORTED -> "Unsupported"
            else -> "Unknown error"
        }
        throwError(Exception(errorMsg))
        return false
    }

    // View events
    override fun onFinishInflate() {
        super.onFinishInflate()
        keepScreenOn = true
        mHandler = Handler()
        mPlayer = MediaPlayer()
        mPlayer!!.setOnPreparedListener(this)
        mPlayer!!.setOnBufferingUpdateListener(this)
        mPlayer!!.setOnCompletionListener(this)
        mPlayer!!.setOnVideoSizeChangedListener(this)
        mPlayer!!.setOnErrorListener(this)
        mPlayer!!.setAudioStreamType(AudioManager.STREAM_MUSIC)

        // Instantiate and add TextureView for rendering
        val textureLp = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        mTextureView = TextureView(context)
        //        mTextureView.setBackgroundColor(Color.BLACK);
        addView(mTextureView, textureLp)
        mTextureView!!.surfaceTextureListener = this
        val li = LayoutInflater.from(context)

        // Inflate and add progress
        mProgressFrame = li.inflate(R.layout.evp_include_progress, this, false)
        addView(mProgressFrame)

        // Instantiate and add click frame (used to toggle controls)
        mClickFrame = FrameLayout(context)
        (mClickFrame as FrameLayout).foreground = Util.resolveDrawable(
            context, androidx.appcompat.R.attr.selectableItemBackground
        )
        addView(
            mClickFrame, ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )
        mClickFrame!!.setOnClickListener(OnClickListener { toggleControls() })

        // Inflate controls
        mControlsFrame = li.inflate(R.layout.evp_include_controls, this, false)
        val controlsLp = LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        controlsLp.gravity = Gravity.BOTTOM
        addView(mControlsFrame, controlsLp)
        if (mControlsDisabled) mControlsFrame!!.setVisibility(GONE)

        // Retrieve controls
        mSeeker = mControlsFrame!!.findViewById<View>(R.id.seeker) as SeekBar
        mLabelPosition = mControlsFrame!!.findViewById<View>(R.id.position) as TextView
        mLabelDuration = mControlsFrame!!.findViewById<View>(R.id.duration) as TextView
        mBtnRestart = mControlsFrame!!.findViewById<View>(R.id.btnRestart) as ImageButton
        mBtnRestart!!.setOnClickListener(this)
        mBtnRetry = mControlsFrame!!.findViewById<View>(R.id.btnRetry) as Button
        mBtnRetry!!.setOnClickListener(this)
        mBtnPlayPause = mControlsFrame!!.findViewById<View>(R.id.btnPlayPause) as ImageButton
        mBtnPlayPause!!.setOnClickListener(this)
        mBtnSubmit = mControlsFrame!!.findViewById<View>(R.id.btnSubmit) as Button
        mBtnSubmit!!.setOnClickListener(this)
        mSeeker!!.setOnSeekBarChangeListener(this)
        setControlsEnabled(false)
        val primaryColor = Util.resolveColor(
            context, androidx.appcompat.R.attr.colorPrimary
        )
        mControlsFrame!!.setBackgroundColor(Util.adjustAlpha(primaryColor, 0.8f))
        val labelColor = if (Util.isColorDark(primaryColor)) Color.WHITE else Color.BLACK
        mLabelPosition!!.setTextColor(labelColor)
        mLabelPosition!!.text = Util.getDurationString(0, false)
        mLabelDuration!!.setTextColor(labelColor)
        mLabelDuration!!.text = Util.getDurationString(0, true)
        invalidateActions()
        prepare()
    }

    override fun onClick(view: View) {
        if (view.id == R.id.btnPlayPause) {
            if (mPlayer!!.isPlaying) {
                pause()
            } else {
                if (mHideControlsOnPlay) hideControls()
                start()
            }
        } else if (view.id == R.id.btnRestart) {
            seekTo(0)
            if (!isPlaying()) start()
        } else if (view.id == R.id.btnRetry) {
            if (mCallback != null) mCallback!!.onRetry(this, mSource)
        } else if (view.id == R.id.btnSubmit) {
            if (mCallback != null) mCallback!!.onSubmit(this, mSource)
        }
    }

    override fun onProgressChanged(seekBar: SeekBar, value: Int, fromUser: Boolean) {
        if (fromUser) seekTo(value)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {
        mWasPlaying = isPlaying()
        if (mWasPlaying) mPlayer!!.pause() // keeps the time updater running, unlike pause()
    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {
        if (mWasPlaying) mPlayer!!.start()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        LOG("Detached from window")
        if (mPlayer != null) {
            stop()
            release()
        }
        mTextureView = null
        mSurface = null
        mSeeker = null
        mLabelPosition = null
        mLabelDuration = null
        mBtnPlayPause = null
        mBtnRestart = null
        mBtnSubmit = null
        mControlsFrame = null
        mClickFrame = null
        mProgressFrame = null
        if (mHandler != null) {
            mHandler!!.removeCallbacks(mUpdateCounters)
            mHandler = null
        }
    }

    private fun invalidateActions() {
        when (mLeftAction) {
            LEFT_ACTION_NONE -> {
                mBtnRetry!!.visibility = GONE
                mBtnRestart!!.visibility = GONE
            }
            LEFT_ACTION_RESTART -> {
                mBtnRetry!!.visibility = GONE
                mBtnRestart!!.visibility = VISIBLE
            }
            LEFT_ACTION_RETRY -> {
                mBtnRetry!!.visibility = VISIBLE
                mBtnRestart!!.visibility = GONE
            }
        }
        when (mRightAction) {
            RIGHT_ACTION_NONE -> mBtnSubmit!!.visibility = GONE
            RIGHT_ACTION_SUBMIT -> mBtnSubmit!!.visibility = VISIBLE
        }
    }

    private fun adjustAspectRatio(
        viewWidth: Int,
        viewHeight: Int,
        videoWidth: Int,
        videoHeight: Int
    ) {
        val aspectRatio = videoHeight.toDouble() / videoWidth
        val newWidth: Int
        val newHeight: Int
        if (viewHeight > (viewWidth * aspectRatio).toInt()) {
            // limited by narrow width; restrict height
            newWidth = viewWidth
            newHeight = (viewWidth * aspectRatio).toInt()
        } else {
            // limited by short height; restrict width
            newWidth = (viewHeight / aspectRatio).toInt()
            newHeight = viewHeight
        }
        val xoff = (viewWidth - newWidth) / 2
        val yoff = (viewHeight - newHeight) / 2
        val txform = Matrix()
        mTextureView!!.getTransform(txform)
        txform.setScale(newWidth.toFloat() / viewWidth, newHeight.toFloat() / viewHeight)
        txform.postTranslate(xoff.toFloat(), yoff.toFloat())
        mTextureView!!.setTransform(txform)
    }

    private fun throwError(e: Exception) {
        if (mCallback != null) mCallback!!.onError(this, e) else throw RuntimeException(e)
    }

    companion object {
        const val LEFT_ACTION_NONE = 0
        const val LEFT_ACTION_RESTART = 1
        const val LEFT_ACTION_RETRY = 2
        const val RIGHT_ACTION_NONE = 3
        const val RIGHT_ACTION_SUBMIT = 4
        private const val UPDATE_INTERVAL = 200

        // Utilities
        private fun LOG(message: String, vararg args: Any) {
            var message: String? = message
            if (args != null) message = String.format(message!!, *args)
            Log.d("EasyVideoPlayer", message!!)
        }
    }
}