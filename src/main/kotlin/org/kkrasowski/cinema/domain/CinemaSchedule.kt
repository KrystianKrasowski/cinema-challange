package org.kkrasowski.cinema.domain

import org.kkrasowski.cinema.domain.RoomOccupation.Attribute
import java.time.LocalDateTime

typealias RoomOccupations = Collection<RoomOccupation>
typealias Rooms = Collection<Room>

class CinemaSchedule(private val version: Long,
                     private val scheduledOccupations: RoomOccupations,
                     private val rooms: Rooms,
                     private val configuration: Configuration) {

    fun schedule(film: Film, roomName: RoomName, startsAt: LocalDateTime): ScheduleResult {
        return createSuccess(rooms.getByName(roomName), film, startsAt)
            .takeIf { it.isValid() }
            ?: ScheduleResult.Failure
    }

    private fun createSuccess(room: Room, film: Film, startsAt: LocalDateTime) = ScheduleResult.Success(
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

    private fun ScheduleResult.Success.isValid() = occupations.all { it.isValid() }

    private fun RoomOccupation.isValid() = scheduledOccupations.noneClashesWith(this) && isWithinValidHours()

    private fun RoomOccupation.isWithinValidHours() = when {
        hasAttribute(Attribute.PREMIERE) -> startsAfterOrAt(configuration.premieresStartHour) && endsBeforeOrAt(configuration.premieresEndHour)
        else -> startsAfterOrAt(configuration.openingHour) && endsBeforeOrAt(configuration.closingHour)
    }
}

fun Rooms.getByName(name: RoomName) = first { it.name == name }

fun RoomOccupations.noneClashesWith(occupation: RoomOccupation) = filter { it.roomName == occupation.roomName }
    .none { it.clashesWith(occupation) }
