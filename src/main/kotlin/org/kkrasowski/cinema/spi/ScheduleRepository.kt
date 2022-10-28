package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.CinemaSchedule
import org.kkrasowski.cinema.domain.RoomOccupation

interface ScheduleRepository {

    fun getSchedule(): CinemaSchedule

    fun getOccupations(): Collection<RoomOccupation>

    fun save(version: Long, occupations: Collection<RoomOccupation>)
}
