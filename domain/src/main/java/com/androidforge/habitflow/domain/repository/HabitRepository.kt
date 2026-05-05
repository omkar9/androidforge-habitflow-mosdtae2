package com.androidforge.habitflow.domain.repository

import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Interface defining the contract for data operations related to habits and their completions.
 * This abstraction allows the domain layer to be independent of the data source (e.g., Room, network).
 */
interface HabitRepository {

    /**
     * Inserts a new habit into the data source.
     * @param habit The [Habit] object to insert. Its `id` will be ignored and a new one generated.
     * @return The ID of the newly created habit.
     */
    suspend fun insertHabit(habit: Habit): Long

    /**
     * Updates an existing habit in the data source.
     * @param habit The [Habit] object with updated details. The `id` must be valid.
     */
    suspend fun updateHabit(habit: Habit)

    /**
     * Deletes a habit and all its associated completion records from the data source.
     * @param habitId The ID of the habit to delete.
     */
    suspend fun deleteHabit(habitId: Long)

    /**
     * Retrieves a specific habit by its ID, including its full completion history.
     * @param habitId The ID of the habit to retrieve.
     * @return A [Flow] emitting the [Habit] object if found, or null.
     */
    fun getHabitWithCompletions(habitId: Long): Flow<Habit?>

    /**
     * Retrieves all active habits, each including its full completion history.
     * This is typically used for the main dashboard where streak calculations are needed.
     * @return A [Flow] emitting a list of [Habit] objects.
     */
    fun getAllHabitsWithCompletions(): Flow<List<Habit>>

    /**
     * Inserts or updates a habit completion record for a specific habit on a given date.
     * @param habitId The ID of the habit.
     * @param date The [LocalDate] of the completion.
     * @param status The [HabitStatus] to set for the habit on that date.
     */
    suspend fun upsertHabitCompletion(habitId: Long, date: LocalDate, status: HabitStatus)

    /**
     * Retrieves the completion status for a specific habit on a given date.
     * @param habitId The ID of the habit.
     * @param date The [LocalDate] to check.
     * @return The [HabitStatus] for the habit on that date, or [HabitStatus.NONE] if no record exists.
     */
    suspend fun getHabitCompletionStatus(habitId: Long, date: LocalDate): HabitStatus
}