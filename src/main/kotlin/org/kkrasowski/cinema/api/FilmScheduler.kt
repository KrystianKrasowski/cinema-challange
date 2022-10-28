package org.kkrasowski.cinema.api

import org.kkrasowski.cinema.domain.FilmTitle
import org.kkrasowski.cinema.domain.RoomName
import org.kkrasowski.cinema.domain.ScheduleResult
import java.time.LocalDateTime

interface FilmScheduler {

    fun schedule(filmTitle: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): ScheduleResult
}
