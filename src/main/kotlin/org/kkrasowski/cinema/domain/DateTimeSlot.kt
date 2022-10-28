package org.kkrasowski.cinema.domain

import java.time.LocalDateTime

data class DateTimeSlot(val name: String, val start: LocalDateTime, val end: LocalDateTime) {

    fun clashesWith(other: DateTimeSlot): Boolean = other.start in start..end || other.end in start..end
}

fun dateTimeSlotOf(name: String, start: LocalDateTime, end: LocalDateTime) = DateTimeSlot(name, start, end)
    .takeIf { it.start < it.end }
    ?: throw IllegalArgumentException("Cannot create date time slot. End time must be greater than start time")
