package com.androidforge.habitflow.data.repository

import com.androidforge.habitflow.data.local.dao.HabitCompletionDao
import com.androidforge.habitflow.data.local.dao.HabitDao
import com.androidforge.habitflow.data.local.entity.HabitCompletionEntity
import com.androidforge.habitflow.data.local.mapper.HabitCompletionMapper
import com.androidforge.habitflow.data.local.mapper.HabitMapper
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import com.androidforge.habitflow.domain.repository.HabitRepository
import com.androidforge.habitflow.domain.usecase.streak.CalculateStreakUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of [HabitRepository] using Room for local data access.
 * It bridges between the domain models and Room entities, handling mapping and data conversion.
 * It also injects [CalculateStreakUseCase] to enrich [Habit] objects with streak data upon retrieval.
 */
@Singleton
class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val habitCompletionDao: HabitCompletionDao,
    private val habitMapper: HabitMapper,
    private val habitCompletionMapper: HabitCompletionMapper,
    private val calculateStreakUseCase: CalculateStreakUseCase
) : HabitRepository {

    override suspend fun insertHabit(habit: Habit): Long {
        val habitEntity = habitMapper.toEntity(habit)
        return habitDao.insertHabit(habitEntity)
    }

    override suspend fun updateHabit(habit: Habit) {
        val habitEntity = habitMapper.toEntity(habit)
        habitDao.updateHabit(habitEntity)
    }

    override suspend fun deleteHabit(habitId: Long) {
        habitDao.deleteHabit(habitId)
        // onDelete = CASCADE in foreign key should handle completions, but explicit is safer
        habitCompletionDao.deleteCompletionsForHabit(habitId)
    }

    override fun getHabitWithCompletions(habitId: Long): Flow<Habit?> {
        return habitDao.getHabitWithCompletions(habitId).map {
            it?.let { habitWithCompletions ->
                val completions = habitWithCompletions.completions.map(habitCompletionMapper::toDomain)
                val completedDates = completions.filter { it.status == HabitStatus.COMPLETED }.map { it.date }.toSet()
                val (currentStreak, longestStreak) = calculateStreakUseCase(
                    habitMapper.frequencyStringToDayOfWeekSet(habitWithCompletions.habit.frequency),
                    completedDates,
                    LocalDate.now()
                )
                habitMapper.toDomain(habitWithCompletions.habit, completions, currentStreak, longestStreak)
            }
        }
    }

    override fun getAllHabitsWithCompletions(): Flow<List<Habit>> {
        return habitDao.getAllHabitsWithCompletions().map {
            it.map { habitWithCompletions ->
                val completions = habitWithCompletions.completions.map(habitCompletionMapper::toDomain)
                val completedDates = completions.filter { it.status == HabitStatus.COMPLETED }.map { it.date }.toSet()
                val (currentStreak, longestStreak) = calculateStreakUseCase(
                    habitMapper.frequencyStringToDayOfWeekSet(habitWithCompletions.habit.frequency),
                    completedDates,
                    LocalDate.now()
                )
                habitMapper.toDomain(habitWithCompletions.habit, completions, currentStreak, longestStreak)
            }
        }
    }

    override suspend fun upsertHabitCompletion(habitId: Long, date: LocalDate, status: HabitStatus) {
        val existingCompletion = habitCompletionDao.getCompletionForHabitAndDate(
            habitId,
            habitCompletionMapper.dateToMillis(date)
        )

        val completionToSave = if (existingCompletion == null) {
            // Insert new record
            HabitCompletionEntity(
                habitId = habitId,
                dateMillis = habitCompletionMapper.dateToMillis(date),
                status = status.name
            )
        } else {
            // Update existing record
            existingCompletion.copy(status = status.name)
        }
        habitCompletionDao.insertCompletion(completionToSave)
    }

    override suspend fun getHabitCompletionStatus(habitId: Long, date: LocalDate): HabitStatus {
        return habitCompletionDao.getCompletionForHabitAndDate(habitId, habitCompletionMapper.dateToMillis(date))
            ?.let { HabitStatus.valueOf(it.status) }
            ?: HabitStatus.NONE
    }
}