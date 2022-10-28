package org.kkrasowski.cinema.domain

import java.time.Duration

data class Room(val name: RoomName, val maintenanceTime: Duration)

data class RoomName(val value: String)

data class RoomOccupation(val roomName: RoomName, val label: Label, val slot: DateTimeSlot, val attributes: Collection<Attribute>) {

    enum class Attribute {
        THE_3D_GLASSES_REQUIRED
    }
}

fun String.toRoomName() = RoomName(this)
