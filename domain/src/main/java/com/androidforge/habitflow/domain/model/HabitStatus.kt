package com.androidforge.habitflow.domain.model

/**
 * Enum representing the completion status of a habit for a given day.
 */
enum class HabitStatus {
    COMPLETED,
    SKIPPED,
    MISSED,
    NONE // Not yet recorded or not applicable for the day
}