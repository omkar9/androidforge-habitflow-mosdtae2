package com.androidforge.habitflow.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.androidforge.habitflow.R
import com.androidforge.habitflow.core.common.Constants
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * A [BroadcastReceiver] to handle scheduled notification triggers and device boot completions.
 * It's responsible for displaying habit reminder notifications and rescheduling all notifications on boot.
 */
@AndroidEntryPoint
class NotificationReceiver : BroadcastReceiver() {

    @Inject
    lateinit var notificationScheduler: NotificationScheduler

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        when (intent.action) {
            Intent.ACTION_BOOT_COMPLETED, Intent.ACTION_MY_PACKAGE_REPLACED -> {
                // Reschedule all notifications after device reboot or app update
                CoroutineScope(Dispatchers.IO).launch {
                    notificationScheduler.rescheduleAllNotifications()
                }
            }
            ACTION_SHOW_HABIT_REMINDER -> {
                val habitId = intent.getLongExtra(Constants.NOTIFICATION_HABIT_ID_KEY, -1L)
                val habitName = intent.getStringExtra(Constants.NOTIFICATION_HABIT_NAME_KEY)

                if (habitId != -1L && habitName != null) {
                    showNotification(context, habitId.toInt(), habitName)
                }
            }
        }
    }

    /**
     * Displays a habit reminder notification.
     */
    private fun showNotification(context: Context, notificationId: Int, habitName: String) {
        createNotificationChannel(context)

        val builder = NotificationCompat.Builder(context, Constants.NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_icon) // Replace with your app's notification icon
            .setContentTitle(context.getString(R.string.notification_title, habitName))
            .setContentText(context.getString(R.string.notification_body, habitName))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true) // Dismiss notification when tapped
            // Add pending intent to open app/habit detail when tapped

        with(NotificationManagerCompat.from(context)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // POST_NOTIFICATIONS permission handled in MainActivity
            }
            notify(notificationId, builder.build())
        }
    }

    /**
     * Creates a notification channel for habit reminders (required for Android 8.0+).
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = Constants.NOTIFICATION_CHANNEL_NAME
            val descriptionText = Constants.NOTIFICATION_CHANNEL_DESCRIPTION
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(Constants.NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val ACTION_SHOW_HABIT_REMINDER = "com.androidforge.habitflow.SHOW_HABIT_REMINDER"
    }
}