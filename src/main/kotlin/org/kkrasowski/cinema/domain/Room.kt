package org.kkrasowski.cinema.domain

import java.time.Duration

data class Room(val name: RoomName, val maintenanceTime: Duration, val occupations: Collection<DateTimeSlot>) {

    fun hasFreeSlotFor(slot: DateTimeSlot): Boolean = occupations.none { it.clashesWith(slot) }
}

data class RoomName(val value: String)

data class RoomOccupation(val roomName: RoomName, val slot: DateTimeSlot)

fun String.toRoomName() = RoomName(this)
