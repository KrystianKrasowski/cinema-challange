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
        val filmSlot = DateTimeSlot(title.value, startsAt, filmEndTime)
        val maintenanceSlot = DateTimeSlot("Maintenance", filmEndTime, maintenanceEndTime)

        return if (room.hasFreeSlotFor(filmSlot) && room.hasFreeSlotFor(maintenanceSlot) && filmSlot.isValidFor(film.type) && maintenanceSlot.endsBeforeOrExactlyAt(closingHour)) {
            schedules.save(listOf(
                RoomOccupation(roomName, filmSlot),
                RoomOccupation(roomName, maintenanceSlot)
            ))

            Seance.SCHEDULED
        } else {
            Seance.DECLINED
        }
    }

    private fun DateTimeSlot.isValidFor(type: Film.Type) = when(type) {
        Film.Type.REGULAR -> startsAfterOrExactlyAt(openingHour) && endsBeforeOrExactlyAt(closingHour)
        Film.Type.PREMIERE -> startsAfterOrExactlyAt(premieresStartHour) && endsBeforeOrExactlyAt(premieresEndHour)
    }
}
