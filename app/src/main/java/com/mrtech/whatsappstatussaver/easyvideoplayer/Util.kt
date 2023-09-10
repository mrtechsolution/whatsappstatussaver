package com.mrtech.whatsappstatussaver.easyvideoplayer

import android.content.Context
import kotlin.jvm.JvmOverloads
import androidx.annotation.AttrRes
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.Drawable
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * @author Aidan Follestad (afollestad)
 */
internal object Util {
    fun getDurationString(durationMs: Long, negativePrefix: Boolean): String {
        return String.format(
            Locale.getDefault(), "%s%02d:%02d",
            if (negativePrefix) "-" else "",
            TimeUnit.MILLISECONDS.toMinutes(durationMs),
            TimeUnit.MILLISECONDS.toSeconds(durationMs) -
                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(durationMs))
        )
    }

    fun isColorDark(color: Int): Boolean {
        val darkness =
            1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255
        return darkness >= 0.5
    }

    fun adjustAlpha(color: Int, factor: Float): Int {
        val alpha = Math.round(Color.alpha(color) * factor)
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)
        return Color.argb(alpha, red, green, blue)
    }

    @JvmOverloads
    fun resolveColor(context: Context, @AttrRes attr: Int, fallback: Int = 0): Int {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            a.getColor(0, fallback)
        } finally {
            a.recycle()
        }
    }

    fun resolveDrawable(context: Context, @AttrRes attr: Int): Drawable? {
        return resolveDrawable(context, attr, null)
    }

    private fun resolveDrawable(
        context: Context,
        @AttrRes attr: Int,
        fallback: Drawable?
    ): Drawable? {
        val a = context.theme.obtainStyledAttributes(intArrayOf(attr))
        return try {
            var d = a.getDrawable(0)
            if (d == null && fallback != null) d = fallback
            d
        } finally {
            a.recycle()
        }
    }
}