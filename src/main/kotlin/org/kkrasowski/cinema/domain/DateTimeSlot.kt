package org.kkrasowski.cinema.domain

import java.time.LocalDateTime

data class DateTimeSlot(val name: String, val start: LocalDateTime, val end: LocalDateTime)
