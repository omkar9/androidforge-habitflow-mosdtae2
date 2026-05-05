package com.androidforge.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.androidforge.habitflow.data.local.entity.HabitCompletionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [HabitCompletionEntity] operations.
 * Provides methods to interact with the 'habit_completions' table in the Room database.
 */
@Dao
interface HabitCompletionDao {

    /**
     * Inserts a new habit completion record into the database.
     * If a record for the same habit and date already exists, it will be replaced.
     * @param completion The [HabitCompletionEntity] to insert.
     * @return The ID of the newly inserted or replaced completion record.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCompletion(completion: HabitCompletionEntity): Long

    /**
     * Updates an existing habit completion record in the database.
     * @param completion The [HabitCompletionEntity] to update.
     * @return The number of rows updated (should be 1).
     */
    @Update
    suspend fun updateCompletion(completion: HabitCompletionEntity): Int

    /**
     * Deletes a specific habit completion record.
     * @param completionId The ID of the completion record to delete.
     * @return The number of rows deleted (should be 1).
     */
    @Query("DELETE FROM habit_completions WHERE id = :completionId")
    suspend fun deleteCompletion(completionId: Long): Int

    /**
     * Deletes all completion records for a given habit.
     * This is typically used when a habit itself is deleted.
     * @param habitId The ID of the habit for which to delete completions.
     * @return The number of rows deleted.
     */
    @Query("DELETE FROM habit_completions WHERE habitId = :habitId")
    suspend fun deleteCompletionsForHabit(habitId: Long): Int

    /**
     * Retrieves a specific habit completion record for a given habit and date.
     * @param habitId The ID of the habit.
     * @param dateMillis The date (in milliseconds since epoch) of the completion record.
     * @return The [HabitCompletionEntity] if found, or null.
     */
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId AND dateMillis = :dateMillis LIMIT 1")
    suspend fun getCompletionForHabitAndDate(habitId: Long, dateMillis: Long): HabitCompletionEntity?

    /**
     * Retrieves all completion records for a specific habit.
     * @param habitId The ID of the habit.
     * @return A [Flow] emitting a list of [HabitCompletionEntity] objects for the specified habit.
     */
    @Query("SELECT * FROM habit_completions WHERE habitId = :habitId ORDER BY dateMillis DESC")
    fun getCompletionsForHabit(habitId: Long): Flow<List<HabitCompletionEntity>>
}