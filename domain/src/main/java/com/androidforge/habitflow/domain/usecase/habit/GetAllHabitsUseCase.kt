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
 * UseCase for retrieving all active habits, including their completion status and streaks.
 * This provides a snapshot of all habits for display on the main dashboard.
 */
class GetAllHabitsUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Retrieves a list of all active habits, enriched with current streak and completion status for today.
     * This method fetches data as a snapshot, suitable for one-time UI updates or initial loads.
     *
     * @return A [Result] emitting a list of [Habit] objects.
     */
    suspend operator fun invoke(): Result<List<Habit>> = withContext(defaultDispatcher) {
        return@withContext try {
            // Collect the first emission from the Flow to get a snapshot
            val habits = habitRepository.getAllHabitsWithCompletions().firstOrNull() ?: emptyList()
            Result.Success(habits)
        } catch (e: Exception) {
            // Distinguish between network errors (e.g., if repository had network access) and local errors
            // For Room, most exceptions are local (e.g., DB corruption, I/O issues)
            Result.Error(e)
        }
    }
}