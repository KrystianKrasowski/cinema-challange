package org.kkrasowski.cinema.domain

import arrow.core.*
import org.kkrasowski.cinema.domain.RoomOccupation.Attribute
import java.time.LocalDateTime

typealias RoomOccupations = Collection<RoomOccupation>
typealias Rooms = Collection<Room>

class CinemaSchedule(private val version: Long,
                     private val scheduledOccupations: RoomOccupations,
                     private val rooms: Rooms,
                     private val configuration: Configuration) {

    fun schedule(film: Film, roomName: RoomName, startsAt: LocalDateTime): Either<Failure, ScheduledSeance> {
        return rooms
            .getByName(roomName)
            .map { createScheduledSeance(it, film, startsAt) }
            .flatMap { it.validate() }
    }

    // TODO: Add comment WHY this is not extracted to separate class
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

    // TODO: Add comment WHY this is not extracted to separate class
    private fun ScheduledSeance.validate() = when {
        clashesWithOtherSeance() -> failureOf("Clashes with other seance or is unavailable")
        isNotWithinPremiereHours() -> failureOf("Is not within premiere hours")
        isNotWithinWorkingHours() -> failureOf("Is not within working hours")
        else -> this.right()
    }

    private fun ScheduledSeance.clashesWithOtherSeance() = occupations
        .any { it.clashesWith(scheduledOccupations) }

    private fun ScheduledSeance.isNotWithinWorkingHours() = occupations
        .any { !it.hasAttribute(Attribute.PREMIERE) && (it.startsBefore(configuration.openingHour) || it.endsAfter(configuration.closingHour)) }

    private fun ScheduledSeance.isNotWithinPremiereHours() = occupations
        .any { it.hasAttribute(Attribute.PREMIERE) && (it.startsBefore(configuration.premieresStartHour) || it.endsAfter(configuration.premieresEndHour)) }

}

private fun failureOf(reason: String) = Failure(reason).left()

// TODO: Move to Room file along with typealias
fun Rooms.getByName(name: RoomName) = firstOrNull { it.name == name }
    ?.right()
    ?: Failure("""There is no such room as "$name".""").left()
