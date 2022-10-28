package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.RoomOccupation

interface ScheduleRepository {

    fun save(occupations: Collection<RoomOccupation>)
}
