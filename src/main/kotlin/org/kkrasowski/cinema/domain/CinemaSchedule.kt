package org.kkrasowski.cinema.domain

import java.time.LocalDateTime

typealias RoomOccupations = Collection<RoomOccupation>
typealias Rooms = Collection<Room>

class CinemaSchedule(private val version: Long,
                     private val occupations: RoomOccupations,
                     private val rooms: Rooms,
                     private val configuration: Configuration
) {

    fun schedule(film: Film, roomName: RoomName, startsAt: LocalDateTime): ScheduleResult {
        val filmOccupation = createFilmOccupation(roomName, film, startsAt)
        val maintenanceOccupation = createMaintenanceOccupation(rooms.getByName(roomName), filmOccupation.endsAt)

        return if (filmOccupation.isValid() && maintenanceOccupation.isValid()) {
            ScheduleResult.Success(version, listOf(filmOccupation, maintenanceOccupation))
        } else {
            ScheduleResult.Failure
        }
    }

    private fun createFilmOccupation(roomName: RoomName, film: Film, startsAt: LocalDateTime): RoomOccupation {
        return RoomOccupation(roomName, labelOf(film), dateTimeSlotOf(startsAt, startsAt + film.time), listOfNotNull(
            create3DGlassesRequiredAttribute(film.display),
            createPremiereAttribute(film.type)
        ))
    }

    private fun create3DGlassesRequiredAttribute(display: Film.Display) = RoomOccupation.Attribute.THE_3D_GLASSES_REQUIRED
        .takeIf { display == Film.Display.DISPLAY_3D }

    private fun createPremiereAttribute(type: Film.Type) = RoomOccupation.Attribute.PREMIERE
        .takeIf { type == Film.Type.PREMIERE }

    private fun createMaintenanceOccupation(room: Room, startsAt: LocalDateTime): RoomOccupation {
        return RoomOccupation(room.name, labelOfMaintenance(), dateTimeSlotOf(startsAt, startsAt + room.maintenanceTime), emptyList())
    }

    private fun RoomOccupation.isValid() = occupations.noneClashesWith(this) && isWithinValidHours()

    private fun RoomOccupation.isWithinValidHours() = when {
        hasAttribute(RoomOccupation.Attribute.PREMIERE) -> startsAfterOrExactlyAt(configuration.premieresStartHour) && endsBeforeOrExactlyAt(configuration.premieresEndHour)
        else -> startsAfterOrExactlyAt(configuration.openingHour) && endsBeforeOrExactlyAt(configuration.closingHour)
    }
}

sealed class ScheduleResult {

    data class Success(val version: Long, val occupations: Collection<RoomOccupation>) : ScheduleResult()
    object Failure : ScheduleResult()
}

fun Rooms.getByName(name: RoomName) = first { it.name == name }

fun RoomOccupations.noneClashesWith(occupation: RoomOccupation) = filter { it.roomName == occupation.roomName }
    .none { it.clashesWith(occupation) }
