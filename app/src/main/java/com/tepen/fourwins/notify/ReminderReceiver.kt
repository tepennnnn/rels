package com.tepen.fourwins.notify

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.tepen.fourwins.MainActivity
import com.tepen.fourwins.R
import com.tepen.fourwins.data.FourWinsDbHelper

class ReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            val db = FourWinsDbHelper(context)
            ReminderScheduler.syncReminders(context, db.getProfile())
            return
        }

        ReminderScheduler.ensureChannel(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val granted = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) return
        }

        val type = intent.getStringExtra(ReminderScheduler.EXTRA_REMINDER_TYPE) ?: ReminderScheduler.TYPE_MORNING
        val (title, body, notifId) = if (type == ReminderScheduler.TYPE_EVENING) {
            Triple(
                "Evening 4-Wins Check-In",
                "Lock in Physical, Mental, Financial & Spiritual wins before lights out.",
                4102
            )
        } else {
            Triple(
                "Time to Stack Your 4 Daily Wins",
                "Start with 10 min walk or today's knee-safe gym session.",
                4101
            )
        }

        val tapIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val tapPi = PendingIntent.getActivity(
            context,
            notifId,
            tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, ReminderScheduler.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_fg)
            .setContentTitle(title)
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(tapPi)
            .build()

        NotificationManagerCompat.from(context).notify(notifId, notification)
    }
}
