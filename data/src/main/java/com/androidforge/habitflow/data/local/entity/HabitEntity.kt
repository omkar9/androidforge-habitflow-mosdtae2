package com.androidforge.habitflow.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing the 'habits' table in the local database.
 *
 * @property id Unique identifier for the habit.
 * @property name The user-defined name of the habit.
 * @property description An optional longer description of the habit.
 * @property frequency A string representation of the days of the week the habit should be performed (e.g., "MON,WED,FRI").
 * @property reminderTimeMillis An optional time (in milliseconds from midnight) for a reminder notification.
 * @property createdAtMillis The creation date of the habit in milliseconds since epoch.
 * @property lastModifiedMillis The last modification date of the habit in milliseconds since epoch.
 * @property isArchived A boolean indicating if the habit is archived.
 */
@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val name: String,
    val description: String,
    val frequency: String, // Stored as comma-separated string (e.g., "MON,WED,FRI")
    val reminderTimeMillis: Long?, // Stored as milliseconds from midnight (0-86399999)
    val createdAtMillis: Long,
    val lastModifiedMillis: Long,
    val isArchived: Boolean
)