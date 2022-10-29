package org.kkrasowski.cinema.application

import arrow.core.*
import org.kkrasowski.cinema.api.FilmScheduler
import org.kkrasowski.cinema.domain.*
import org.kkrasowski.cinema.spi.FilmsCatalogue
import org.kkrasowski.cinema.spi.ScheduleRepository
import java.time.LocalDateTime

class FilmSchedulerImpl(private val films: FilmsCatalogue,
                        private val scheduleRepository: ScheduleRepository) : FilmScheduler {

    private val cinemaSchedule
        get() = scheduleRepository.getSchedule()

    override fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Either<Failure, ScheduledSeance> {
        return getFilmByTitle(filmTitle)
            .flatMap { cinemaSchedule.schedule(it, roomName, startsAt) }
            .flatMap { scheduleRepository.save(it) }
    }

    private fun getFilmByTitle(title: FilmTitle) = films
        .find(title)
        ?.right()
        ?: Failure("""Film catalogue does not contain the film "$title".""").left()
}
