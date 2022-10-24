package org.kkrasowski.cinema

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

class CinemaSchedule(private val rooms: Rooms,
                     private val seances: ScheduledSeances,
                     private val openingHour: TemporalAmount,
                     private val closingHour: TemporalAmount) {

    fun schedule(film: Film, room: RoomName, startTime: LocalDateTime): Seance {
        return Seance.Scheduled(rooms.getByName(room), film, startTime)
            .takeIf { isValid(it) }
            ?: Seance.Declined
    }

    private fun isValid(seance: Seance.Scheduled) = seances.haveFreeSlotFor(seance)
            && seance.startsAfter(openingHour)
            && seance.endsBefore(closingHour)
}

data class Rooms(private val rooms: List<Room>) {

    fun getByName(name: RoomName): Room = rooms.first { it.name == name }
}

data class ScheduledSeances(private val seances: List<Seance.Scheduled>) {

    fun haveFreeSlotFor(seance: Seance.Scheduled): Boolean = seances.none { it.roomName == seance.roomName && it.clashesWith(seance) }
}

data class Room(val name: RoomName, val maintenanceTime: Duration)
