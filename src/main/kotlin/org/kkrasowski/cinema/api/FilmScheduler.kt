package org.kkrasowski.cinema.api

import arrow.core.Either
import arrow.core.Validated
import org.kkrasowski.cinema.domain.*
import java.time.LocalDateTime

interface FilmScheduler {

    fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Either<Failure, ScheduledSeance>
}
