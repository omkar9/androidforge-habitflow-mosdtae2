package com.androidforge.habitflow.data.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.androidforge.habitflow.core.common.Constants
import com.androidforge.habitflow.core.notifications.NotificationReceiver
import com.androidforge.habitflow.core.notifications.NotificationScheduler
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.usecase.habit.GetAllHabitsUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Android-specific implementation of [NotificationScheduler] using [AlarmManager].
 * Schedules and cancels habit reminder notifications.
 */
@Singleton
class AndroidNotificationScheduler @Inject constructor(
    private val context: Context,
    private val getAllHabitsUseCase: GetAllHabitsUseCase, // Inject use case to get all habits
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : NotificationScheduler {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val TAG = "NotificationScheduler"

    override fun scheduleNotification(habit: Habit) {
        habit.reminderTime ?: run {
            Log.d(TAG, "No reminder time for habit ${habit.name}, canceling any existing notification.")
            cancelNotification(habit.id)
            return
        }

        val now = LocalDate.now()
        val reminderDateTime = LocalDateTime.of(now, habit.reminderTime)

        // If reminder time is already past for today, schedule for tomorrow
        val triggerTime = if (reminderDateTime.isBefore(LocalDateTime.now())) {
            reminderDateTime.plusDays(1)
        } else {
            reminderDateTime
        }

        // Only schedule if today is a frequency day, or tomorrow is.
        val isScheduledToday = habit.frequency.contains(now.dayOfWeek)
        val isScheduledTomorrow = habit.frequency.contains(now.plusDays(1).dayOfWeek)

        if (!isScheduledToday && !isScheduledTomorrow) {
            Log.d(TAG, "Habit ${habit.name} is not scheduled for today or tomorrow, canceling any existing notification.")
            cancelNotification(habit.id)
            return
        }

        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SHOW_HABIT_REMINDER
            putExtra(Constants.NOTIFICATION_HABIT_ID_KEY, habit.id)
            putExtra(Constants.NOTIFICATION_HABIT_NAME_KEY, habit.name)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habit.id.toInt(), // Use habit ID as request code for uniqueness
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )

        val triggerAtMillis = triggerTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                Log.w(TAG, "Cannot schedule exact alarms. User needs to grant permission.")
                // Optionally, show a message to the user or fall back to inexact alarms
            }
        }

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAtMillis,
            pendingIntent
        )
        Log.d(TAG, "Scheduled notification for habit ${habit.name} at ${triggerTime}")
    }

    override fun cancelNotification(habitId: Long) {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_SHOW_HABIT_REMINDER
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            habitId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        )
        pendingIntent?.let { alarmManager.cancel(it) }
        Log.d(TAG, "Cancelled notification for habit ID $habitId")
    }

    override suspend fun rescheduleAllNotifications() = withContext(ioDispatcher) {
        Log.d(TAG, "Rescheduling all notifications...")
        // Fetch all habits to schedule notifications for them
        when (val result = getAllHabitsUseCase()) {
            is com.androidforge.habitflow.core.common.Result.Success -> {
                result.data.forEach { habit ->
                    scheduleNotification(habit)
                }
                Log.d(TAG, "Successfully rescheduled ${result.data.size} notifications.")
            }
            is com.androidforge.habitflow.core.common.Result.Error -> {
                Log.e(TAG, "Failed to reschedule notifications: ${result.exception.message}")
            }
            is com.androidforge.habitflow.core.common.Result.Offline -> {
                Log.w(TAG, "Could not reschedule notifications due to offline status. Will retry later.")
            }
            else -> { /* Loading state not relevant here */ }
        }
    }
}