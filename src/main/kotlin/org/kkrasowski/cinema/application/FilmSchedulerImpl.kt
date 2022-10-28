package org.kkrasowski.cinema.application

import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.RoomsRepository
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.Duration
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val schedules: ScheduleRepository,
                        private val rooms: RoomsRepository) : FilmScheduler {

    // TODO: Could be in some configuration SPI
    private val openingHour = Duration.parse("PT8H")
    private val closingHour = Duration.parse("PT22H")
    private val premieresStartHour = Duration.parse("PT17H")
    private val premieresEndHour = Duration.parse("PT21H")

    override fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance {
        val scheduledFilm = createFilmOccupation(roomName, films.find(filmTitle), startsAt)
        val scheduledMaintenance = createMaintenanceOccupation(rooms.getByName(roomName), scheduledFilm.endsAt)

        return if (scheduledFilm.isValid() && scheduledMaintenance.isValid()) {
            schedules.save(listOf(
                scheduledFilm,
                scheduledMaintenance
            ))

            Seance.Scheduled
        } else {
            Seance.Declined
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

    private fun RoomOccupation.isValid(): Boolean {
        val occupations = schedules.getScheduleFor(roomName)
        return occupations.none { it.clashesWith(this) } && isWithinValidHours()
    }

    private fun RoomOccupation.isWithinValidHours() = when {
        hasAttribute(RoomOccupation.Attribute.PREMIERE) -> startsAfterOrExactlyAt(premieresStartHour) && endsBeforeOrExactlyAt(premieresEndHour)
        else -> startsAfterOrExactlyAt(openingHour) && endsBeforeOrExactlyAt(closingHour)
    }
}
