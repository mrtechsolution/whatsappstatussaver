package com.mrtech.whatsappstatussaver.service.reciever

import android.app.Notification
import android.os.Environment
import com.mrtech.whatsappstatussaver.service.reciever.NotificationReceiver
import android.app.NotificationManager
import android.os.AsyncTask
import androidx.core.content.ContextCompat
import com.mrtech.whatsappstatussaver.R
import com.mrtech.whatsappstatussaver.HelperMethods
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mrtech.whatsappstatussaver.service.reciever.NotificationReceiver.TransferAll
import java.io.File
import java.lang.Exception

class NotificationReceiver : BroadcastReceiver() {
    private val ExternalStorageDirectoryPath =
        Environment.getExternalStorageDirectory().absolutePath
    private val targetPath =
        StringBuffer().append(ExternalStorageDirectoryPath).append("/WhatsApp/Media/.Statuses/")
            .toString()
    private val allFiles = File(targetPath).listFiles()

    inner class TransferAll(
        private val notifActionReceiver: NotificationReceiver,
        private val context: Context,
        private val notifManager: NotificationManager
    ) : AsyncTask<Void?, String?, Boolean?>() {
        override fun onPreExecute() {
            notifManager.cancel(0)
            notifManager.cancelAll()
            val builder = Notification.Builder(context)
            builder.setContentText("Downloading Statuses")
            builder.setColor(ContextCompat.getColor(context, R.color.colorAccent))
            builder.setSmallIcon(R.drawable.notif)
            builder.setProgress(100, 30, true)
            builder.setOngoing(true)
            notifManager.notify(420, builder.build())
            super.onPreExecute()
        }

        protected override fun doInBackground(vararg voidArr: Void?): Boolean? {
            return try {
                val allFiles = notifActionReceiver.allFiles
                for (file in allFiles) {
                    val str = file.name.toString()
                    if (str.endsWith(".jpg") || str.endsWith(".mp4") || str.endsWith(".gif")) {
                        val helperMethods = HelperMethods(context)
                        HelperMethods.transfer(file)
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

        override fun onPostExecute(bool: Boolean?) {
            var builder: Notification.Builder
            var activity: PendingIntent?
            if (bool == null) {
                Log.d("", "Boolean was Null... Strange.")
            }
            if (bool!!) {
                notifManager.cancel(420)
                try {
                    activity = PendingIntent.getActivity(
                        context,
                        4896,
                        Intent(
                            context,
                            Class.forName("com.mrtech.whatsappstatussaver.MainActivity")
                        ),
                        0
                    )
                    builder = Notification.Builder(context)
                    builder.setContentText("All Statuses Saved! ✓").setContentIntent(activity)
                        .setColor(
                            ContextCompat.getColor(
                                context, R.color.colorAccent
                            )
                        ).setSmallIcon(R.drawable.notif)
                    notifManager.notify(4896, builder.build())
                } catch (e: Throwable) {
                    throw NoClassDefFoundError(e.message)
                }
            }
            if (!bool!!) {
                notifManager.cancel(420)
                try {
                    activity = PendingIntent.getActivity(
                        context,
                        4896,
                        Intent(
                            context,
                            Class.forName("com.mrtech.whatsappstatussaver.MainActivity")
                        ),
                        0
                    )
                    builder = Notification.Builder(context)
                    builder.setContentText("There was a problem Saving All of the Statuses!")
                        .setContentIntent(activity).setColor(
                        ContextCompat.getColor(
                            context, R.color.colorAccent
                        )
                    ).setSmallIcon(R.drawable.notif)
                    notifManager.notify(4896, builder.build())
                } catch (e2: Throwable) {
                    throw NoClassDefFoundError(e2.message)
                }
            }
            super.onPostExecute(bool)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val stringExtra = intent.getStringExtra("actionMode")
        if (stringExtra == "saveAll") {
            TransferAll(this, context, notificationManager).execute(*arrayOfNulls<Void>(0))
        }
        if (stringExtra == "openApp") {
            notificationManager.cancel(0)
            notificationManager.cancelAll()
            closePanel(context)
            try {
                val intent2 =
                    Intent(context, Class.forName("com.mrtech.whatsappstatussaver.MainActivity"))
                //                intent2.addFlags(335577088);
                context.startActivity(intent2)
            } catch (e: Throwable) {
                throw NoClassDefFoundError(e.message)
            }
        }
    }

    fun closePanel(context: Context) {
        context.sendBroadcast(Intent("android.intent.action.CLOSE_SYSTEM_DIALOGS"))
    }
}