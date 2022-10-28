package org.kkrasowski.cinema.domain

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

data class DateTimeSlot(val name: String, val start: LocalDateTime, val end: LocalDateTime) {

    private val date = start.truncatedTo(ChronoUnit.DAYS)

    fun clashesWith(other: DateTimeSlot): Boolean = other.start in start..end || other.end in start..end

    fun startsAfterOrExactlyAt(time: Duration): Boolean = start >= date + time

    fun endsBeforeOrExactlyAt(time: Duration): Boolean = end <= date + time
}

fun dateTimeSlotOf(name: String, start: LocalDateTime, end: LocalDateTime) = DateTimeSlot(name, start, end)
    .takeIf { it.start < it.end }
    ?: throw IllegalArgumentException("Cannot create date time slot. End time must be greater than start time")
