package org.kkrasowski.cinema.domain

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

data class DateTimeSlot(val start: LocalDateTime, val end: LocalDateTime) {

    private val date = start.truncatedTo(ChronoUnit.DAYS)

    fun clashesWith(other: DateTimeSlot): Boolean = other.start in start..end || other.end in start..end

    fun startsAfterOrExactlyAt(time: TemporalAmount): Boolean = start >= date + time

    fun startsBefore(time: TemporalAmount): Boolean = start < date + time

    fun endsBeforeOrExactlyAt(time: TemporalAmount): Boolean = end <= date + time

    fun endsAfter(time: TemporalAmount): Boolean = end > date + time
}

fun dateTimeSlotOf(start: LocalDateTime, end: LocalDateTime) = DateTimeSlot(start, end)
    .takeIf { it.start < it.end }
    ?: throw IllegalArgumentException("Cannot create date time slot. End time must be greater than start time")
