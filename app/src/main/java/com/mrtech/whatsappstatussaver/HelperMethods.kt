package com.mrtech.whatsappstatussaver

import android.content.Context
import android.content.Intent
import android.media.MediaScannerConnection
import android.media.MediaScannerConnection.OnScanCompletedListener
import android.net.Uri
import android.os.Build.VERSION
import android.os.Environment
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel

/**
 * Created by umer on 23-Apr-18.
 */
class HelperMethods {

    constructor( classContext: Context) {
        Companion.classContext = classContext
    }
    companion object {
        private var classContext: Context? = null
        fun transfer(file: File) {
            try {
                val stringBuffer =
                    Environment.getExternalStorageDirectory().absolutePath + "/StorySaver/"
                copyFile(file, File(stringBuffer + file.name))
                if (VERSION.SDK_INT >= 19) {
                    MediaScannerConnection.scanFile(
                        Companion.classContext,
                        arrayOf(stringBuffer + file.name),
                        null as Array<String?>?,
                        null as OnScanCompletedListener?
                    )
                    return
                }
                val intent = Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE")
                intent.data = Uri.fromFile(File(stringBuffer + file.name))
                Companion.classContext!!.sendBroadcast(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Status Saver", " " + e.message)
            }
        }

        @Throws(IOException::class)
        fun copyFile(file: File?, file2: File) {
            Log.e("Copy", "error")
            var th: Throwable?
            var th2: Throwable?
            if (!file2.parentFile.exists()) {
                file2.parentFile.mkdirs()
            }
            if (!file2.exists()) {
                file2.createNewFile()
            }
            var fileChannel = null as FileChannel?
            var fileChannel2 = null as FileChannel?
            var channel: FileChannel?
            try {
                channel = FileInputStream(file).channel
                try {
                    fileChannel = FileOutputStream(file2).channel
                } catch (th3: Throwable) {
                    th = th3
                    channel?.close()
                    fileChannel2?.close()
                    throw th
                }
                try {
                    fileChannel.transferFrom(channel, 0L, channel.size())
                    channel?.close()
                    fileChannel?.close()
                } catch (th4: Throwable) {
                    th2 = th4
                    fileChannel2 = fileChannel
                    th = th2
                    channel?.close()
                    fileChannel2?.close()
                    throw th
                }
            } catch (th5: Throwable) {
                th2 = th5
                channel = fileChannel
                th = th2
                channel?.close()
                fileChannel2?.close()
                try {
                    throw th
                } catch (throwable: Throwable) {
                    throwable.printStackTrace()
                }
            }
        }

        fun getLatestFilefromDir(str: String?): File? {
            val listFiles = File(str).listFiles()
            if (listFiles == null || listFiles.size == 0) {
                return null as File?
            }
            var file = listFiles[0]
            for (i in 1 until listFiles.size) {
                if (file.lastModified() < listFiles[i].lastModified()) {
                    file = listFiles[i]
                }
            }
            return file
        }
    }
}