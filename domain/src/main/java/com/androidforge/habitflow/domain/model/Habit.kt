package com.androidforge.habitflow.domain.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime

/**
 * Represents a single habit in the application's domain layer.
 *
 * @property id Unique identifier for the habit. Null for new habits before persistence.
 * @property name The user-defined name of the habit.
 * @property description An optional longer description of the habit.
 * @property frequency A set of [DayOfWeek] indicating on which days the habit should be performed.
 * @property reminderTime An optional [LocalTime] when a reminder notification should be triggered.
 * @property createdAt The [LocalDate] when the habit was first created.
 * @property lastModified The [LocalDate] when the habit was last modified.
 * @property isArchived A boolean indicating if the habit is archived (not actively tracked).
 * @property currentStreak The number of consecutive days the habit has been successfully completed, respecting frequency.
 * @property longestStreak The longest streak ever achieved for this habit.
 * @property completedToday The [HabitStatus] for the current day (derived, not persisted directly).
 * @property completions A list of [HabitCompletion] records for this habit.
 */
data class Habit(
    val id: Long = 0L,
    val name: String,
    val description: String,
    val frequency: Set<DayOfWeek>,
    val reminderTime: LocalTime?,
    val createdAt: LocalDate,
    val lastModified: LocalDate,
    val isArchived: Boolean = false,
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val completedToday: HabitStatus = HabitStatus.NONE,
    val completions: List<HabitCompletion> = emptyList()
)