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
 * UseCase for creating and persisting a new habit.
 * Encapsulates the business logic for adding a habit, including validation.
 */
class AddHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Creates a new habit and persists it.
     *
     * @param name The name of the habit.
     * @param description The description of the habit.
     * @param frequency The set of days of the week the habit should be performed.
     * @param reminderTime The optional reminder time for the habit.
     * @return A [Result] indicating success or failure of the operation.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(
        name: String,
        description: String,
        frequency: Set<DayOfWeek>,
        reminderTime: LocalTime?
    ): Result<Unit> = withContext(defaultDispatcher) {
        if (name.isBlank()) {
            return@withContext Result.Error(IllegalArgumentException("Habit name cannot be empty."))
        }
        if (frequency.isEmpty()) {
            return@withContext Result.Error(IllegalArgumentException("Please select at least one day for the habit frequency."))
        }

        val newHabit = Habit(
            name = name,
            description = description,
            frequency = frequency,
            reminderTime = reminderTime,
            createdAt = LocalDate.now(),
            lastModified = LocalDate.now(),
            isArchived = false // New habits are always active
        )

        return@withContext try {
            habitRepository.insertHabit(newHabit)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}