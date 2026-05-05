package com.androidforge.habitflow.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.androidforge.habitflow.data.local.dao.HabitCompletionDao
import com.androidforge.habitflow.data.local.dao.HabitDao
import com.androidforge.habitflow.data.local.entity.HabitCompletionEntity
import com.androidforge.habitflow.data.local.entity.HabitEntity
import com.androidforge.habitflow.data.local.util.Converters

/**
 * Room database class for HabitFlow application.
 * Defines the entities, DAOs, and type converters for local persistence.
 */
@Database(
    entities = [
        HabitEntity::class,
        HabitCompletionEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HabitFlowDatabase : RoomDatabase() {

    /**
     * Provides the [HabitDao] for interacting with the 'habits' table.
     * @return An instance of [HabitDao].
     */
    abstract fun habitDao(): HabitDao

    /**
     * Provides the [HabitCompletionDao] for interacting with the 'habit_completions' table.
     * @return An instance of [HabitCompletionDao].
     */
    abstract fun habitCompletionDao(): HabitCompletionDao
}