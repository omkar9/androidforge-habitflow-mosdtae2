package com.androidforge.habitflow.domain.usecase.habit

import com.androidforge.habitflow.core.common.Result
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

/**
 * UseCase for updating the details of an existing habit.
 * Encapsulates the business logic for editing a habit, including validation.
 */
class EditHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Updates an existing habit with new details.
     *
     * @param id The ID of the habit to update.
     * @param name The new name of the habit.
     * @param description The new description of the habit.
     * @param frequency The new set of days of the week the habit should be performed.
     * @param reminderTime The new optional reminder time for the habit.
     * @return A [Result] indicating success or failure of the operation.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(
        id: Long,
        name: String,
        description: String,
        frequency: Set<DayOfWeek>,
        reminderTime: LocalTime?
    ): Result<Unit> = withContext(defaultDispatcher) {
        if (id <= 0) {
            return@withContext Result.Error(IllegalArgumentException("Invalid habit ID for update."))
        }
        if (name.isBlank()) {
            return@withContext Result.Error(IllegalArgumentException("Habit name cannot be empty."))
        }
        if (frequency.isEmpty()) {
            return@withContext Result.Error(IllegalArgumentException("Please select at least one day for the habit frequency."))
        }

        // Fetch existing habit to preserve other fields like createdAt, isArchived, etc.
        val existingHabitResult = habitRepository.getHabitWithCompletions(id)
            .toResult() // Convert Flow to a single Result snapshot

        val existingHabit = when (existingHabitResult) {
            is Result.Success -> existingHabitResult.data
            is Result.Error -> return@withContext Result.Error(existingHabitResult.exception)
            is Result.Loading -> return@withContext Result.Error(IllegalStateException("Habit data still loading."))
            is Result.Offline -> return@withContext Result.Offline(existingHabitResult.exception)
        }

        if (existingHabit == null) {
            return@withContext Result.Error(NoSuchElementException("Habit with ID $id not found."))
        }

        val updatedHabit = existingHabit.copy(
            name = name,
            description = description,
            frequency = frequency,
            reminderTime = reminderTime,
            lastModified = LocalDate.now()
        )

        return@withContext try {
            habitRepository.updateHabit(updatedHabit)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}

// Helper extension function to convert Flow<T?> to suspend Result<T?>
// This is a simplified approach. For production, consider a more robust Flow-to-Result conversion.
private suspend fun <T> Flow<T?>.toResult(): Result<T?> {
    return try {
        val data = this.firstOrNull()
        Result.Success(data)
    } catch (e: Exception) {
        Result.Error(e)
    }
}