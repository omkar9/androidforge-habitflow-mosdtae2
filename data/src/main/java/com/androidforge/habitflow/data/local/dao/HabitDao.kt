package com.androidforge.habitflow.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.androidforge.habitflow.data.local.entity.HabitEntity
import com.androidforge.habitflow.data.local.entity.HabitWithCompletions
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for [HabitEntity] operations.
 * Provides methods to interact with the 'habits' table in the Room database.
 */
@Dao
interface HabitDao {

    /**
     * Inserts a new habit into the database.
     * @param habit The [HabitEntity] to insert.
     * @return The ID of the newly inserted habit.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    /**
     * Updates an existing habit in the database.
     * @param habit The [HabitEntity] to update.
     * @return The number of rows updated (should be 1).
     */
    @Update
    suspend fun updateHabit(habit: HabitEntity): Int

    /**
     * Deletes a habit from the database.
     * @param habitId The ID of the habit to delete.
     * @return The number of rows deleted (should be 1).
     */
    @Query("DELETE FROM habits WHERE id = :habitId")
    suspend fun deleteHabit(habitId: Long): Int

    /**
     * Retrieves a single habit by its ID.
     * @param habitId The ID of the habit to retrieve.
     * @return A [Flow] emitting the [HabitEntity] if found, or null.
     */
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitById(habitId: Long): Flow<HabitEntity?>

    /**
     * Retrieves all habits from the database.
     * @return A [Flow] emitting a list of all [HabitEntity] objects.
     */
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY lastModifiedMillis DESC")
    fun getAllHabits(): Flow<List<HabitEntity>>

    /**
     * Retrieves a single habit along with all its associated completion records.
     * This uses a Room @Transaction to ensure atomicity and proper data retrieval.
     * @param habitId The ID of the habit to retrieve.
     * @return A [Flow] emitting a [HabitWithCompletions] object, or null.
     */
    @Transaction
    @Query("SELECT * FROM habits WHERE id = :habitId")
    fun getHabitWithCompletions(habitId: Long): Flow<HabitWithCompletions?>

    /**
     * Retrieves all non-archived habits along with their associated completion records.
     * This is useful for displaying a dashboard where each habit needs its full history for streak calculation.
     * @return A [Flow] emitting a list of [HabitWithCompletions] objects.
     */
    @Transaction
    @Query("SELECT * FROM habits WHERE isArchived = 0 ORDER BY name ASC") // Or by lastModified, or custom order
    fun getAllHabitsWithCompletions(): Flow<List<HabitWithCompletions>>
}