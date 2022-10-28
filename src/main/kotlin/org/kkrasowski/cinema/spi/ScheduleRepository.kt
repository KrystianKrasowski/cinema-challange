package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.CinemaSchedule
import org.kkrasowski.cinema.domain.RoomOccupation

interface ScheduleRepository {

    fun getSchedule(): CinemaSchedule

    fun save(version: Long, occupations: Collection<RoomOccupation>)
}
