package org.kkrasowski.cinema.domain

import arrow.core.Validated
import arrow.core.invalid
import arrow.core.valid
import org.kkrasowski.cinema.domain.RoomOccupation.Attribute
import java.time.LocalDateTime

typealias RoomOccupations = Collection<RoomOccupation>
typealias Rooms = Collection<Room>

class CinemaSchedule(private val version: Long,
                     private val scheduledOccupations: RoomOccupations,
                     private val rooms: Rooms,
                     private val configuration: Configuration) {

    fun schedule(film: Film, roomName: RoomName, startsAt: LocalDateTime): Validated<Failure, ScheduledSeance> {
        return rooms
            .getByName(roomName)
            .let { createScheduledSeance(it, film, startsAt) }
            .validate()
    }

    private fun createScheduledSeance(room: Room, film: Film, startsAt: LocalDateTime) = ScheduledSeance(
        version = version + 1,
        occupations = listOf(
            RoomOccupation(
                roomName = room.name,
                label = labelOf(film),
                slot = dateTimeSlotOf(
                    start = startsAt,
                    end = startsAt + film.time
                ),
                attributes = listOfNotNull(
                    create3DGlassesRequiredAttribute(film.display),
                    createPremiereAttribute(film.type)
                )
            ),
            RoomOccupation(
                roomName = room.name,
                label = labelOfMaintenance(),
                slot = dateTimeSlotOf(
                    start = startsAt + film.time,
                    end = startsAt + film.time + room.maintenanceTime
                ),
                attributes = emptyList()
            )
        )
    )

    private fun create3DGlassesRequiredAttribute(display: Film.Display) = Attribute.THE_3D_GLASSES_REQUIRED
        .takeIf { display == Film.Display.DISPLAY_3D }

    private fun createPremiereAttribute(type: Film.Type) = Attribute.PREMIERE
        .takeIf { type == Film.Type.PREMIERE }

    private fun ScheduledSeance.validate() = when {
        clashesWithOtherSeance() -> Failure("Clashes with other seance or is unavailable").invalid()
        isNotWithinPremiereHours() -> Failure("Is not within premiere hours").invalid()
        isNotWithinWorkingHours() -> Failure("Is not within working hours").invalid()
        else -> this.valid()
    }

    private fun ScheduledSeance.clashesWithOtherSeance() = occupations
        .any { it.clashesWith(scheduledOccupations) }

    private fun ScheduledSeance.isNotWithinWorkingHours() = occupations
        .any { !it.hasAttribute(Attribute.PREMIERE) && (it.startsBefore(configuration.openingHour) || it.endsAfter(configuration.closingHour)) }

    private fun ScheduledSeance.isNotWithinPremiereHours() = occupations
        .any { it.hasAttribute(Attribute.PREMIERE) && (it.startsBefore(configuration.premieresStartHour) || it.endsAfter(configuration.premieresEndHour)) }

}

// TODO: Move to Room file along with typealias
fun Rooms.getByName(name: RoomName) = first { it.name == name }
