package org.kkrasowski.cinema.domain

import java.time.Duration

data class Room(val name: RoomName, val maintenanceTime: Duration)

data class RoomName(val value: String)

data class RoomOccupation(val roomName: RoomName, val slot: DateTimeSlot)

fun String.toRoomName() = RoomName(this)
