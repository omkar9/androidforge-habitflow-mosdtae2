package com.androidforge.habitflow.domain.usecase.habit

import com.androidforge.habitflow.core.common.Result
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for fetching a specific habit by its unique identifier, including its full completion history.
 * This provides a snapshot of a single habit's details.
 */
class GetHabitByIdUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Retrieves a specific habit by its ID, including its completion history and streaks.
     * This method fetches data as a snapshot, suitable for one-time UI updates or initial loads.
     *
     * @param habitId The ID of the habit to retrieve.
     * @return A [Result] emitting the [Habit] object if found, or an error/null if not.
     */
    suspend operator fun invoke(habitId: Long): Result<Habit> = withContext(defaultDispatcher) {
        if (habitId <= 0) {
            return@withContext Result.Error(IllegalArgumentException("Invalid habit ID."))
        }

        return@withContext try {
            // Collect the first emission from the Flow to get a snapshot
            val habit = habitRepository.getHabitWithCompletions(habitId).firstOrNull()
            if (habit != null) {
                Result.Success(habit)
            } else {
                Result.Error(NoSuchElementException("Habit with ID $habitId not found."))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}