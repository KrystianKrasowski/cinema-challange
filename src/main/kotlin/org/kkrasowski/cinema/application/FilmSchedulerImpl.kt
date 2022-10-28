package org.kkrasowski.cinema.application

import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.RoomsRepository
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val schedules: ScheduleRepository,
                        private val rooms: RoomsRepository) : FilmScheduler {

    override fun schedule(title: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance {
        val film = films.find(title)
        val room = rooms.getByName(roomName)
        val filmEndTime = startsAt + film.time
        val maintenanceEndTime = filmEndTime + room.maintenanceTime
        val filmSlot = DateTimeSlot(title.value, startsAt, filmEndTime)
        val maintenanceSlot = DateTimeSlot("Maintenance", filmEndTime, maintenanceEndTime)

        return if (areValid(room, filmSlot, maintenanceSlot)) {
            schedules.save(listOf(
                RoomOccupation(roomName, filmSlot),
                RoomOccupation(roomName, maintenanceSlot)
            ))

            Seance.SCHEDULED
        } else {
            Seance.DECLINED
        }
    }

    private fun areValid(room: Room, filmSlot: DateTimeSlot, maintenanceSlot: DateTimeSlot): Boolean {
        return room.hasFreeSlotFor(filmSlot) && room.hasFreeSlotFor(maintenanceSlot)
    }
}
