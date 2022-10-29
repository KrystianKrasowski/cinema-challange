package org.kkrasowski.cinema.application

import arrow.core.Either
import arrow.core.Validated
import arrow.core.flatMap
import arrow.core.getOrElse
import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val scheduleRepository: ScheduleRepository) : FilmScheduler {

    override fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Either<Failure, ScheduledSeance> {
        return scheduleRepository
            .getSchedule()
            .schedule(films.find(filmTitle), roomName, startsAt)
            .toEither()
            .flatMap { scheduleRepository.save(it) }
    }
}
