package com.mrtech.whatsappstatussaver.service

import android.app.Notification
import com.mrtech.whatsappstatussaver.HelperMethods
import android.os.IBinder
import com.mrtech.whatsappstatussaver.service.NotificationService.ServiceBinder
import android.app.NotificationManager
import com.mrtech.whatsappstatussaver.service.NotificationService
import android.content.Intent
import android.os.Environment
import android.app.PendingIntent
import android.app.Service
import android.os.Binder
import android.util.Log
import com.mrtech.whatsappstatussaver.R
import androidx.core.content.ContextCompat
import java.io.File
import java.lang.NullPointerException
import java.util.*

class NotificationService : Service() {
    val UNLOCK_BUSINESS_STATUSES = "business_statuses"
    private var helperMethods: HelperMethods? = null
    private val mBinder: IBinder = ServiceBinder(this)
    private var mNM: NotificationManager? = null
    private var notif: Notification? = null

    private inner class FileObserver(private val notificationService: NotificationService) :
        TimerTask() {
        override fun run() {
            CheckForNewFiles()
        }
    }

    inner class ServiceBinder(val service: NotificationService) : Binder()

    override fun onCreate() {
        Log.i("NotifyService", "onCreate()")
        helperMethods = HelperMethods(this)
        mNM = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        Timer().scheduleAtFixedRate(FileObserver(this), 0L, 5000L)
    }

    override fun onStartCommand(intent: Intent, i: Int, i2: Int): Int {
        Log.i(
            "LocalService",
            StringBuffer().append(
                StringBuffer().append(
                    StringBuffer().append("Received start id ").append(i2).toString()
                ).append(": ").toString()
            ).append(intent).toString()
        )
        return START_STICKY
    }

    override fun onBind(intent: Intent): IBinder? {
        return mBinder
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            val intent = Intent(this, Class.forName("com.mrtech.service.reciever.BootReceiver"))
            intent.action = "com.mrtech.notif.onDestroyed"
            sendBroadcast(intent)
        } catch (e: Throwable) {
        }
    }

    private fun showImageStatusNotification() {
        val stringBuffer =
            StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
                .append("/WhatsApp/Media/.Statuses/").toString()
        builder = Notification.Builder(this)
        try {
            builder!!.setContentTitle("Status Saver")
                .setContentText("New Image Status, Refresh Now!").setContentIntent(
                PendingIntent.getActivity(
                    this,
                    0,
                    Intent(this, Class.forName("com.mrtech.whatsappstatussaver.MainActivity")),
                    0
                )
            ).setSmallIcon(R.drawable.notif).setShowWhen(true)
                .setVibrate(longArrayOf(100L, 100L, 100L, 100L))
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            notif = builder!!.notification
            val notification = notif
            notification!!.flags = notification.flags or 8
            mNM!!.notify(0, notif)
            getSharedPreferences("latestFile", 0).edit().putLong(
                "latestFile",
                HelperMethods.getLatestFilefromDir(stringBuffer)!!.lastModified()
            ).apply()
        } catch (e: Throwable) {
            throw NoClassDefFoundError(e.message)
        }
    }

    private fun showVideoStatusNotification() {
        val stringBuffer =
            StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
                .append("/WhatsApp/Media/.Statuses/").toString()
        builder = Notification.Builder(this)
        try {
            builder!!.setContentTitle("Status Saver")
                .setContentText("New Video Status, Refresh Now!").setContentIntent(
                PendingIntent.getActivity(
                    this,
                    1,
                    Intent(this, Class.forName("com.mrtech.whatsappstatussaver.MainActivity")),
                    0
                )
            ).setSmallIcon(R.drawable.notif).setShowWhen(true)
                .setVibrate(longArrayOf(100L, 100L, 100L, 100L))
                .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            notif = builder!!.notification
            val notification = notif
            notification!!.flags = notification.flags or 8
            mNM!!.notify(1, notif)
            getSharedPreferences("latestFile", 0).edit().putLong(
                "latestFile",
                HelperMethods.getLatestFilefromDir(stringBuffer)!!.lastModified()
            ).apply()
        } catch (e: Throwable) {
            throw NoClassDefFoundError(e.message)
        }
    }

    fun CheckForNewFiles() {
        var i = 0
        val checkSelfPermission =
            ContextCompat.checkSelfPermission(this, "android.permission.WRITE_EXTERNAL_STORAGE")
        if (ContextCompat.checkSelfPermission(
                this,
                "android.permission.READ_EXTERNAL_STORAGE"
            ) == 0 && checkSelfPermission == 0
        ) {
            var fileArr = null as Array<File>?
            val stringBuffer =
                StringBuffer().append(Environment.getExternalStorageDirectory().absolutePath)
                    .append("/WhatsApp/Media/.Statuses/").toString()
            val file = File(stringBuffer)
            if (file.isFile) {
                file.delete()
            }
            if (!file.isDirectory) {
                file.mkdirs()
            }
            if (!file.exists()) {
                file.mkdirs()
            }
            if (file.isDirectory) {
                try {
                    fileArr = File(stringBuffer).listFiles()
                } catch (e: NullPointerException) {
                    e.printStackTrace()
                }
            }
            val date = Date(
                getSharedPreferences("latestFile", 0).getLong(
                    "latestFile",
                    System.currentTimeMillis()
                )
            )
            if (fileArr != null) {
                count = fileArr.size
                Log.d("Status Files Count", StringBuffer().append("").append(count).toString())
            }
            if (fileArr != null && count > 0) {
                while (i < fileArr.size) {
                    val file2 = fileArr[i]
                    if (Date(file2.lastModified()).compareTo(date) > 0) {
                        if (file2.name.endsWith(".jpg") || file2.name.endsWith(".jpeg") || file2.name.endsWith(
                                ".png"
                            )
                        ) {
                            showImageStatusNotification()
                        }
                        if (file2.name.endsWith(".mp4") || file2.name.endsWith(".avi") || file2.name.endsWith(
                                ".mkv"
                            ) || file2.name.endsWith(".gif")
                        ) {
                            showVideoStatusNotification()
                        }
                    }
                    i++
                }
            }
        }
    }

    companion object {
        const val INTENT_NOTIFY = "com.mrtech.services.INTENT_NOTIFY"
        private var count = 0
        var builder: Notification.Builder? = null
    }
}