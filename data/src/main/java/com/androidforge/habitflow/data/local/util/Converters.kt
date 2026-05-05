package com.androidforge.habitflow.data.local.util

import androidx.room.TypeConverter
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/**
 * Room Type Converters for [LocalDate] and [LocalTime].
 * These convert Java Time API objects to and from Long (milliseconds since epoch or milliseconds from midnight)
 * for storage in the Room database.
 */
class Converters {

    /**
     * Converts a [LocalDate] to a Long representing milliseconds since epoch at UTC.
     * @param date The [LocalDate] to convert.
     * @return Milliseconds since epoch, or null if input is null.
     */
    @TypeConverter
    fun localDateToMillis(date: LocalDate?): Long? {
        return date?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

    /**
     * Converts a Long representing milliseconds since epoch at UTC to a [LocalDate].
     * @param millis The milliseconds to convert.
     * @return [LocalDate], or the current date if input is null.
     */
    @TypeConverter
    fun millisToLocalDate(millis: Long?): LocalDate {
        return millis?.let { Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate() } ?: LocalDate.now()
    }

    /**
     * Converts a [LocalTime] to a Long representing milliseconds from midnight.
     * @param time The [LocalTime] to convert.
     * @return Milliseconds from midnight (0-86399999), or null if input is null.
     */
    @TypeConverter
    fun localTimeToMillis(time: LocalTime?): Long? {
        return time?.toNanoOfDay()?.div(1_000_000L)
    }

    /**
     * Converts a Long representing milliseconds from midnight to a [LocalTime].
     * @param millis The milliseconds to convert.
     * @return [LocalTime], or null if input is null.
     */
    @TypeConverter
    fun millisToLocalTime(millis: Long?): LocalTime? {
        return millis?.let { LocalTime.ofNanoOfDay(it * 1_000_000L) }
    }
}