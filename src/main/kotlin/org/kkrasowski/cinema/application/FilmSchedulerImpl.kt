package org.kkrasowski.cinema.application

import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val scheduleRepository: ScheduleRepository) : FilmScheduler {

    override fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance {
        val film = films.find(filmTitle)
        val cinemaSchedule = scheduleRepository.getSchedule()
        val schedule = cinemaSchedule.schedule(film, roomName, startsAt)

        return if (schedule is ScheduleResult.Success) {
            scheduleRepository.save(schedule.version, schedule.occupations)
            Seance.Scheduled
        } else {
            Seance.Declined
        }
    }
}
