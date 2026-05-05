package com.androidforge.habitflow.core.common

/**
 * Centralized file for application-wide constants.
 */
object Constants {
    const val DATABASE_NAME = "habit_flow_db"

    // AdMob
    const val ADMOB_APP_ID = "ca-app-pub-3940256099942544~3347511713" // Test App ID
    const val ADMOB_BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/6300978111" // Test Banner Ad Unit ID
    const val ADMOB_INTERSTITIAL_AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712" // Test Interstitial Ad Unit ID

    // Notifications
    const val NOTIFICATION_CHANNEL_ID = "habit_reminders_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Habit Reminders"
    const val NOTIFICATION_CHANNEL_DESCRIPTION = "Reminders for your daily habits"
    const val NOTIFICATION_REQUEST_CODE = 1001
    const val NOTIFICATION_HABIT_ID_KEY = "habit_id"
    const val NOTIFICATION_HABIT_NAME_KEY = "habit_name"
}