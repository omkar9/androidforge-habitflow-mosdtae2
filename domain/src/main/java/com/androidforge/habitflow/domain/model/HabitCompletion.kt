package com.androidforge.habitflow.domain.model

import java.time.LocalDate

/**
 * Represents a single completion record for a specific habit on a given date.
 *
 * @property id Unique identifier for the completion record. Null for new records before persistence.
 * @property habitId The ID of the habit this completion belongs to.
 * @property date The [LocalDate] on which the habit's status was recorded.
 * @property status The [HabitStatus] for the habit on that specific date.
 */
data class HabitCompletion(
    val id: Long = 0L,
    val habitId: Long,
    val date: LocalDate,
    val status: HabitStatus
)