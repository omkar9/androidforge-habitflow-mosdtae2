package com.androidforge.habitflow.domain.usecase.streak

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

/**
 * UseCase for calculating the current and longest streaks for a given habit.
 * Takes into account the habit's frequency.
 */
class CalculateStreakUseCase @Inject constructor(
    private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
) {
    /**
     * Calculates the current and longest streaks for a habit.
     *
     * @param frequency The days of the week the habit is scheduled for.
     * @param completedDates A set of dates when the habit was successfully completed.
     * @param today The current date, used as the reference point for calculating the current streak.
     * @return A [Pair] where the first element is the current streak and the second is the longest streak.
     */
    suspend operator fun invoke(
        frequency: Set<DayOfWeek>,
        completedDates: Set<LocalDate>,
        today: LocalDate
    ): Pair<Int, Int> = withContext(defaultDispatcher) {
        if (frequency.isEmpty()) {
            return@withContext Pair(0, 0) // Cannot calculate streak for a habit with no frequency
        }

        // Helper to check if a day is a scheduled day for the habit
        fun isScheduledDay(date: LocalDate) = frequency.contains(date.dayOfWeek)

        var currentStreak = 0
        var longestStreak = 0
        var tempStreak = 0

        // Calculate current streak (from 'today' backwards)
        var checkDate = today
        var foundFirstCompletion = false

        // Find the most recent scheduled day that should have been completed
        var mostRecentScheduledDay: LocalDate? = null
        var daysBack = 0
        while (daysBack < 365) { // Look back up to a year to find a relevant scheduled day
            val date = today.minusDays(daysBack.toLong())
            if (isScheduledDay(date)) {
                mostRecentScheduledDay = date
                break
            }
            daysBack++
        }

        if (mostRecentScheduledDay != null) {
            checkDate = mostRecentScheduledDay
            while (true) {
                if (!isScheduledDay(checkDate)) {
                    checkDate = checkDate.minusDays(1)
                    continue // Skip days not in frequency
                }

                if (checkDate.isAfter(today)) { // Don't count future days for current streak
                    checkDate = checkDate.minusDays(1)
                    continue
                }

                if (completedDates.contains(checkDate)) {
                    currentStreak++
                    foundFirstCompletion = true
                } else {
                    // If it's a scheduled day but not completed, current streak breaks
                    if (foundFirstCompletion || checkDate.isBefore(today)) { // Only break if we've started counting or if it's a past day
                        break
                    }
                }

                checkDate = checkDate.minusDays(1)
                if (checkDate.isBefore(today.minusYears(1))) break // Limit lookback
            }
        }


        // Calculate longest streak (iterate through all recorded completions or a reasonable historical window)
        val sortedCompletionDates = completedDates.sorted()
        if (sortedCompletionDates.isEmpty()) {
            return@withContext Pair(currentStreak, 0)
        }

        var lastCompletedDay: LocalDate? = null

        // Iterate through days from the earliest completion up to today (or last completion + a few days)
        // to accurately capture gaps and scheduled days.
        val earliestDate = sortedCompletionDates.first().minusDays(30) // Look a bit before first completion
        val latestDate = today.plusDays(1) // Include today

        var dateIterator = earliestDate
        while (!dateIterator.isAfter(latestDate)) {
            if (isScheduledDay(dateIterator)) {
                if (completedDates.contains(dateIterator)) {
                    tempStreak++
                } else {
                    // If it's a scheduled day but not completed, the streak breaks
                    longestStreak = maxOf(longestStreak, tempStreak)
                    tempStreak = 0
                }
            } else {
                // If it's not a scheduled day, it doesn't break the streak, but also doesn't count towards it.
                // The streak carries over to the next scheduled day.
            }
            dateIterator = dateIterator.plusDays(1)
        }
        longestStreak = maxOf(longestStreak, tempStreak) // Capture the last streak if it's the longest

        Pair(currentStreak, longestStreak)
    }
}