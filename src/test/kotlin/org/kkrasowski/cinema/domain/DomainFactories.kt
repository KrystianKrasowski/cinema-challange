package org.kkrasowski.cinema.domain

import java.time.Duration
import java.time.LocalDateTime

fun filmOf(title: String, duration: String) = Film(
    title = FilmTitle(title),
    time = Duration.parse(duration)
)

fun roomOf(name: String, maintenanceTime: String, occupations: Collection<DateTimeSlot> = listOf()) = Room(
    name = RoomName(name),
    maintenanceTime = Duration.parse(maintenanceTime),
    occupations = occupations
)

fun dateTimeSlotOf(name: String, start: String, end: String) = dateTimeSlotOf(
    name = name,
    start = LocalDateTime.parse(start),
    end = LocalDateTime.parse(end)
)
