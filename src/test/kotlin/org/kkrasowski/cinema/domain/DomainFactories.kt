package org.kkrasowski.cinema.domain

import java.time.Duration
import java.time.LocalDateTime

fun filmOf(title: String, duration: String) = Film(
    title = title.toFilmTitle(),
    time = Duration.parse(duration),
    type = Film.Type.REGULAR,
    display = Film.Display.DISPLAY_2D
)

fun film3DOf(title: String, duration: String) = Film(
    title = title.toFilmTitle(),
    time = Duration.parse(duration),
    type = Film.Type.REGULAR,
    display = Film.Display.DISPLAY_3D
)

fun premiereFilmOf(title: String, duration: String) = Film(
    title = title.toFilmTitle(),
    time = Duration.parse(duration),
    type = Film.Type.PREMIERE,
    display = Film.Display.DISPLAY_2D
)

fun roomOf(name: String, maintenanceTime: String) = Room(
    name = name.toRoomName(),
    maintenanceTime = Duration.parse(maintenanceTime)
)

fun roomOccupationOf(roomName: String, label: String, slot: DateTimeSlot, attributes: Collection<RoomOccupation.Attribute> = emptyList()) = RoomOccupation(
    roomName = roomName.toRoomName(),
    label = label.toLabel(),
    slot = slot,
    attributes = attributes
)

fun dateTimeSlotOf(start: String, end: String) = dateTimeSlotOf(
    start = LocalDateTime.parse(start),
    end = LocalDateTime.parse(end)
)

fun ScheduleResult.asSuccess() = this as ScheduleResult.Success
