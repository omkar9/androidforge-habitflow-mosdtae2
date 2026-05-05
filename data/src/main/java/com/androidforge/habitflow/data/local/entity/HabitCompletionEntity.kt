package com.androidforge.habitflow.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.androidforge.habitflow.domain.model.HabitStatus

/**
 * Room Entity representing the 'habit_completions' table in the local database.
 * Includes a foreign key constraint to the 'habits' table.
 *
 * @property id Unique identifier for the completion record.
 * @property habitId The ID of the habit this completion belongs to.
 * @property dateMillis The date of the completion record in milliseconds since epoch.
 * @property status The [HabitStatus] for the habit on that specific date, stored as a string.
 */
@Entity(
    tableName = "habit_completions",
    foreignKeys = [
        ForeignKey(
            entity = HabitEntity::class,
            parentColumns = ["id"],
            childColumns = ["habitId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["habitId", "dateMillis"], unique = true)] // Ensure unique completion per habit per day
)
data class HabitCompletionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val habitId: Long,
    val dateMillis: Long,
    val status: String // Stored as string representation of HabitStatus enum
)