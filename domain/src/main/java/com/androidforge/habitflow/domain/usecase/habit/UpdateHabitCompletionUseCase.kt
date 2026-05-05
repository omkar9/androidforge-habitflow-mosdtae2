package com.androidforge.habitflow.domain.usecase.habit

import com.androidforge.habitflow.core.common.Result
import com.androidforge.habitflow.domain.model.HabitStatus
import com.androidforge.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase for marking a habit as completed, skipped, or missed for a specific day.
 * It toggles the status of a habit completion record.
 */
class UpdateHabitCompletionUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Toggles the completion status of a habit for a given date.
     * The status cycles through [COMPLETED] -> [SKIPPED] -> [MISSED] -> [NONE].
     *
     * @param habitId The ID of the habit to update.
     * @param date The [LocalDate] for which to toggle the completion status.
     * @return A [Result] indicating success or failure of the operation.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(habitId: Long, date: LocalDate): Result<Unit> = withContext(defaultDispatcher) {
        if (habitId <= 0) {
            return@withContext Result.Error(IllegalArgumentException("Invalid habit ID."))
        }

        return@withContext try {
            val currentStatus = habitRepository.getHabitCompletionStatus(habitId, date)
            val newStatus = when (currentStatus) {
                HabitStatus.NONE -> HabitStatus.COMPLETED
                HabitStatus.COMPLETED -> HabitStatus.SKIPPED
                HabitStatus.SKIPPED -> HabitStatus.MISSED
                HabitStatus.MISSED -> HabitStatus.NONE
            }
            habitRepository.upsertHabitCompletion(habitId, date, newStatus)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}