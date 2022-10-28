package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.RoomName
import org.kkrasowski.cinema.domain.RoomOccupation

interface ScheduleRepository {

    fun getScheduleFor(roomName: RoomName): Collection<RoomOccupation>

    fun save(occupations: Collection<RoomOccupation>)
}
