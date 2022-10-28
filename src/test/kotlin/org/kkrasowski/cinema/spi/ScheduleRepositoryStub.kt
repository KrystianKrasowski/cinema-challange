package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.RoomName
import org.kkrasowski.cinema.domain.RoomOccupation
import org.kkrasowski.cinema.domain.toRoomName

class ScheduleRepositoryStub : ScheduleRepository {

    private val occupations = mutableListOf<RoomOccupation>()

    override fun getScheduleFor(roomName: RoomName): Collection<RoomOccupation> = occupations.filter { it.roomName == roomName }

    override fun save(occupations: Collection<RoomOccupation>) {
        occupations.forEach { this.occupations.add(it) }
    }

    fun getOccupationsForRoom(name: String): List<RoomOccupation> = getScheduleFor(name.toRoomName()).toList()

    fun containsOccupation(occupation: RoomOccupation) = apply { occupations.add(occupation) }
}
