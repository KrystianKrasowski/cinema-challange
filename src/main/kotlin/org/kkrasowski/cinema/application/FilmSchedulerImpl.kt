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

    override fun schedule(title: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance {
        val film = films.find(title)
        val room = rooms.getByName(roomName)
        val filmEndTime = startsAt + film.time
        val maintenanceEndTime = filmEndTime + room.maintenanceTime
        val filmSlot = DateTimeSlot(startsAt, filmEndTime)
        val maintenanceSlot = DateTimeSlot(filmEndTime, maintenanceEndTime)
        val occupations = schedules.getScheduleFor(roomName)

        return if (occupations.hasFreeSlotFor(filmSlot) && occupations.hasFreeSlotFor(maintenanceSlot) && filmSlot.isValidFor(film.type) && maintenanceSlot.endsBeforeOrExactlyAt(closingHour)) {
            schedules.save(listOf(
                RoomOccupation(roomName, labelOf(film), filmSlot, createAttributesFor(film)),
                RoomOccupation(roomName, labelOfMaintenance(), maintenanceSlot, emptyList())
            ))

            Seance.SCHEDULED
        } else {
            Seance.DECLINED
        }
    }

    private fun Collection<RoomOccupation>.hasFreeSlotFor(slot: DateTimeSlot) = map { it.slot }
        .none { it.clashesWith(slot) }

    private fun createAttributesFor(film: Film) = listOf(RoomOccupation.Attribute.THE_3D_GLASSES_REQUIRED)
        .takeIf { film.display == Film.Display.DISPLAY_3D }
        ?: emptyList()

    private fun DateTimeSlot.isValidFor(type: Film.Type) = when(type) {
        Film.Type.REGULAR -> startsAfterOrExactlyAt(openingHour) && endsBeforeOrExactlyAt(closingHour)
        Film.Type.PREMIERE -> startsAfterOrExactlyAt(premieresStartHour) && endsBeforeOrExactlyAt(premieresEndHour)
    }
}
