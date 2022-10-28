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

    override fun schedule(title: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance {
        val film = films.find(title)
        val room = rooms.getByName(roomName)
        val filmEndTime = startsAt + film.time
        val maintenanceEndTime = filmEndTime + room.maintenanceTime
        val filmSlot = DateTimeSlot(title.value, startsAt, filmEndTime)
        val maintenanceSlot = DateTimeSlot("Maintenance", filmEndTime, maintenanceEndTime)

        return if (filmSlot.isValidFor(room) && maintenanceSlot.isValidFor(room)) {
            schedules.save(listOf(
                RoomOccupation(roomName, filmSlot),
                RoomOccupation(roomName, maintenanceSlot)
            ))

            Seance.SCHEDULED
        } else {
            Seance.DECLINED
        }
    }

    private fun DateTimeSlot.isValidFor(room: Room) = room.hasFreeSlotFor(this)
            && startsAfterOrExactlyAt(openingHour)
            && endsBeforeOrExactlyAt(closingHour)
}
