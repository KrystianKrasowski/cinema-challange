package org.kkrasowski.cinema.api

import arrow.core.Either
import org.kkrasowski.cinema.domain.Failure
import org.kkrasowski.cinema.domain.FilmTitle
import org.kkrasowski.cinema.domain.RoomName
import org.kkrasowski.cinema.domain.ScheduledSeance
import java.time.LocalDateTime

interface FilmScheduler {

    fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Either<Failure, ScheduledSeance>
}
