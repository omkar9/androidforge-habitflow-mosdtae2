package com.androidforge.habitflow.data.local.mapper

import com.androidforge.habitflow.data.local.entity.HabitEntity
import com.androidforge.habitflow.data.local.util.Converters
import com.androidforge.habitflow.domain.model.Habit
import com.androidforge.habitflow.domain.model.HabitCompletion
import com.androidforge.habitflow.domain.model.HabitStatus
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Utility object for mapping between domain [Habit] and data [HabitEntity].
 * Handles conversions for frequency (Set<DayOfWeek> to String) and reminder time (LocalTime to Long).
 */
@Singleton
class HabitMapper @Inject constructor() {

    private val converters = Converters()

    /**
     * Converts a [Habit] domain model to a [HabitEntity] for persistence.
     * Derived fields like streaks and `completedToday` are not part of the entity.
     */
    fun toEntity(domain: Habit): HabitEntity {
        return HabitEntity(
            id = domain.id,
            name = domain.name,
            description = domain.description,
            frequency = frequencyDayOfWeekSetToString(domain.frequency),
            reminderTimeMillis = converters.localTimeToMillis(domain.reminderTime),
            createdAtMillis = converters.localDateToMillis(domain.createdAt),
            lastModifiedMillis = converters.localDateToMillis(domain.lastModified),
            isArchived = domain.isArchived
        )
    }

    /**
     * Converts a [HabitEntity] and its associated [HabitCompletion]s to a [Habit] domain model.
     * This method also calculates `currentStreak`, `longestStreak`, and `completedToday` based on completions.
     *
     * @param entity The [HabitEntity] to convert.
     * @param completions The list of [HabitCompletion]s associated with this habit.
     * @param currentStreak The calculated current streak for the habit.
     * @param longestStreak The calculated longest streak for the habit.
     * @return The fully constructed [Habit] domain model.
     */
    fun toDomain(
        entity: HabitEntity,
        completions: List<HabitCompletion>,
        currentStreak: Int,
        longestStreak: Int
    ): Habit {
        val today = LocalDate.now()
        val completedToday = completions.firstOrNull { it.date == today }?.status ?: HabitStatus.NONE

        return Habit(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            frequency = frequencyStringToDayOfWeekSet(entity.frequency),
            reminderTime = converters.millisToLocalTime(entity.reminderTimeMillis),
            createdAt = converters.millisToLocalDate(entity.createdAtMillis),
            lastModified = converters.millisToLocalDate(entity.lastModifiedMillis),
            isArchived = entity.isArchived,
            currentStreak = currentStreak,
            longestStreak = longestStreak,
            completedToday = completedToday,
            completions = completions
        )
    }

    /**
     * Converts a Set of [DayOfWeek] to a comma-separated String for persistence.
     * E.g., {MONDAY, WEDNESDAY} -> "MON,WED"
     */
    fun frequencyDayOfWeekSetToString(frequency: Set<DayOfWeek>): String {
        return frequency.sortedBy { it.value }.joinToString(",") { it.name.substring(0, 3) }
    }

    /**
     * Converts a comma-separated String of day abbreviations to a Set of [DayOfWeek].
     * E.g., "MON,WED" -> {MONDAY, WEDNESDAY}
     */
    fun frequencyStringToDayOfWeekSet(frequencyString: String): Set<DayOfWeek> {
        if (frequencyString.isBlank()) return emptySet()
        return frequencyString.split(",").mapNotNull { abbreviation ->
            when (abbreviation) {
                "MON" -> DayOfWeek.MONDAY
                "TUE" -> DayOfWeek.TUESDAY
                "WED" -> DayOfWeek.WEDNESDAY
                "THU" -> DayOfWeek.THURSDAY
                "FRI" -> DayOfWeek.FRIDAY
                "SAT" -> DayOfWeek.SATURDAY
                "SUN" -> DayOfWeek.SUNDAY
                else -> null
            }
        }.toSet()
    }
}