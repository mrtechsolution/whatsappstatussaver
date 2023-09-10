package com.mrtech.whatsappstatussaver.service.reciever

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.mrtech.whatsappstatussaver.service.NotificationService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED" || intent.action == "com.mrtech.notif.onDestroyed") {
            try {
                context.startService(Intent(context, NotificationService::class.java))
            } catch (e: Throwable) {
                throw NoClassDefFoundError(e.message)
            }
        }
    }
}