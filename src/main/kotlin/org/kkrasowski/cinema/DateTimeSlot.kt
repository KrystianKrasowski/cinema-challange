package org.kkrasowski.cinema

import java.time.LocalDateTime

data class DateTimeSlot(val start: LocalDateTime, val end: LocalDateTime) {

    fun clashesWith(other: DateTimeSlot): Boolean {
        return other.end in start..end || other.start in start..end
    }
}
