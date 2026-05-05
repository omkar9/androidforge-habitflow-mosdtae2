package com.androidforge.habitflow.domain.usecase.habit

import com.androidforge.habitflow.core.common.Result
import com.androidforge.habitflow.domain.repository.HabitRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * UseCase for removing an existing habit and its associated completions.
 */
class DeleteHabitUseCase @Inject constructor(
    private val habitRepository: HabitRepository,
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Deletes a habit by its ID.
     *
     * @param habitId The ID of the habit to delete.
     * @return A [Result] indicating success or failure of the operation.
     *         On success, it returns [Unit].
     */
    suspend operator fun invoke(habitId: Long): Result<Unit> = withContext(defaultDispatcher) {
        if (habitId <= 0) {
            return@withContext Result.Error(IllegalArgumentException("Invalid habit ID for deletion."))
        }

        return@withContext try {
            habitRepository.deleteHabit(habitId)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e)
        }
    }
}