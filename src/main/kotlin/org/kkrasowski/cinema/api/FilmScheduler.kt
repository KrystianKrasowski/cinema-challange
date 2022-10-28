package org.kkrasowski.cinema.api

import org.kkrasowski.cinema.domain.FilmTitle
import org.kkrasowski.cinema.domain.RoomName
import org.kkrasowski.cinema.domain.Seance
import java.time.LocalDateTime

interface FilmScheduler {

    fun schedule(title: FilmTitle, roomName: RoomName, startsAt: LocalDateTime): Seance
}

