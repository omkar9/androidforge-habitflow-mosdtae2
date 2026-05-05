package com.androidforge.habitflow.data.local.mapper

import com.androidforge.habitflow.data.local.entity.HabitCompletionEntity
import com.androidforge.habitflow.data.local.util.Converters
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility object for mapping between domain [HabitCompletion] and data [HabitCompletionEntity].
 * Handles conversions for date (LocalDate to Long) and status (HabitStatus to String).
 */
@Singleton
class HabitCompletionMapper @Inject constructor() {

    private val converters = Converters()

    /**
     * Converts a [HabitCompletion] domain model to a [HabitCompletionEntity] for persistence.
     */
    fun toEntity(domain: HabitCompletion): HabitCompletionEntity {
        return HabitCompletionEntity(
            id = domain.id,
            habitId = domain.habitId,
            dateMillis = converters.localDateToMillis(domain.date),
            status = domain.status.name
        )
    }

    /**
     * Converts a [HabitCompletionEntity] to a [HabitCompletion] domain model.
     */
    fun toDomain(entity: HabitCompletionEntity): HabitCompletion {
        return HabitCompletion(
            id = entity.id,
            habitId = entity.habitId,
            date = converters.millisToLocalDate(entity.dateMillis),
            status = HabitStatus.valueOf(entity.status)
        )
    }

    /**
     * Converts a [LocalDate] to milliseconds since epoch.
     */
    fun dateToMillis(date: LocalDate): Long {
        return converters.localDateToMillis(date)
    }

    /**
     * Converts milliseconds since epoch to a [LocalDate].
     */
    fun millisToDate(millis: Long): LocalDate {
        return converters.millisToLocalDate(millis)
    }
}