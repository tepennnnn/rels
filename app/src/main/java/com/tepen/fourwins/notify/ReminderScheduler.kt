package com.tepen.fourwins.notify

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.tepen.fourwins.model.UserProfile
import java.util.Calendar

object ReminderScheduler {

    const val CHANNEL_ID = "four_wins_reminders"
    const val EXTRA_REMINDER_TYPE = "reminder_type"
    const val TYPE_MORNING = "morning"
    const val TYPE_EVENING = "evening"
    private const val REQ_MORNING = 4001
    private const val REQ_EVENING = 4002

    fun ensureChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "4 Daily Wins & Workout Reminders",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Daily training kickoff and evening 4-Wins check-in reminders."
                enableVibration(true)
            }
            val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    fun syncReminders(context: Context, profile: UserProfile) {
        ensureChannel(context)
        if (profile.morningReminderEnabled) {
            scheduleDaily(context, REQ_MORNING, TYPE_MORNING, profile.morningHour, profile.morningMinute)
        } else {
            cancel(context, REQ_MORNING, TYPE_MORNING)
        }

        if (profile.eveningReminderEnabled) {
            scheduleDaily(context, REQ_EVENING, TYPE_EVENING, profile.eveningHour, profile.eveningMinute)
        } else {
            cancel(context, REQ_EVENING, TYPE_EVENING)
        }
    }

    private fun scheduleDaily(
        context: Context,
        requestCode: Int,
        type: String,
        hour: Int,
        minute: Int
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_TYPE, type)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pi
        )
    }

    private fun cancel(context: Context, requestCode: Int, type: String) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            putExtra(EXTRA_REMINDER_TYPE, type)
        }
        val pi = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        ) ?: return
        alarmManager.cancel(pi)
    }
}
