package org.kkrasowski.cinema

import java.time.Duration
import java.time.LocalDateTime
import java.time.temporal.TemporalAmount

class CinemaSchedule(private val rooms: Rooms,
                     private val seances: ScheduledSeances,
                     private val openingHour: TemporalAmount,
                     private val closingHour: TemporalAmount,
                     private val premieresStartsAt: TemporalAmount,
                     private val premieresEndsAt: TemporalAmount) {

    fun schedule(film: Film, roomName: RoomName, startTime: LocalDateTime): Seance {
        val room = rooms.getByName(roomName)
        return Seance.Scheduled(room, film, startTime)
            .takeIf { isValid(it) && room.isAvailableFor(it) }
            ?: Seance.Declined
    }

    private fun isValid(seance: Seance.Scheduled) = seances.haveFreeSlotFor(seance)
            && seance.isWithinValidHours()

    private fun Seance.Scheduled.isWithinValidHours() = when (filmType) {
        Film.FilmType.BASIC -> startsAfter(openingHour) && endsBefore(closingHour)
        Film.FilmType.PREMIERE -> startsAfter(premieresStartsAt) && endsBefore(premieresEndsAt)
    }
}

data class Rooms(private val rooms: List<Room>) {

    fun getByName(name: RoomName): Room = rooms.first { it.name == name }
}

data class ScheduledSeances(private val seances: List<Seance.Scheduled>) {

    fun haveFreeSlotFor(seance: Seance.Scheduled): Boolean = seances.none { it.roomName == seance.roomName && it.clashesWith(seance) }
}

data class Room(val name: RoomName, val maintenanceTime: Duration, val unavailableTimes: List<DateTimeSlot>) {

    fun isAvailableFor(seance: Seance.Scheduled): Boolean = unavailableTimes
        .none { it.clashesWith(seance.dateTimeSlot) }
}
