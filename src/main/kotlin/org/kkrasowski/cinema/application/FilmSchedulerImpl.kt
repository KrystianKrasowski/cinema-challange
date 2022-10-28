package org.kkrasowski.cinema.application

import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val scheduleRepository: ScheduleRepository) : FilmScheduler {

    override fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): ScheduleResult {
        return scheduleRepository
            .getSchedule()
            .schedule(films.find(filmTitle), roomName, startsAt)
            .onSuccess { scheduleRepository.save(it.version, it.occupations) }
    }
}
