package org.kkrasowski.cinema

import java.time.Duration
import java.time.LocalDateTime

class CinemaSchedule(private val rooms: Rooms) {

    fun schedule(film: Film, room: RoomName, startTime: LocalDateTime): Seance {
        return Seance.Scheduled(rooms.getByName(room), film, startTime)
    }
}

data class Rooms(private val rooms: List<Room>) {

    fun getByName(name: RoomName) = rooms
        .first { it.name == name }
}

data class Room(val name: RoomName, val maintenanceTime: Duration)
