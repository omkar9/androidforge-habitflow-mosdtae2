package com.androidforge.habitflow.core.notifications

import com.androidforge.habitflow.domain.model.Habit

/**
 * Interface for scheduling and canceling local notifications for habit reminders.
 * This abstraction allows the domain layer to trigger notifications without direct
 * dependency on Android's AlarmManager or NotificationManager.
 */
interface NotificationScheduler {

    /**
     * Schedules a reminder notification for a specific habit.
     * If a notification for this habit is already scheduled, it will be updated.
     * @param habit The [Habit] for which to schedule a reminder. Requires `id` and `reminderTime`.
     */
    fun scheduleNotification(habit: Habit)

    /**
     * Cancels any pending reminder notification for a specific habit.
     * @param habitId The ID of the habit for which to cancel the notification.
     */
    fun cancelNotification(habitId: Long)

    /**
     * Reschedules all active habit reminders, typically called on app startup or after a device reboot.
     * This ensures that notifications persist across reboots and are up-to-date with current habit settings.
     */
    suspend fun rescheduleAllNotifications()
}