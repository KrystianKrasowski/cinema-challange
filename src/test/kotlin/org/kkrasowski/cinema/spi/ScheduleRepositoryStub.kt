package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.DateTimeSlot
import org.kkrasowski.cinema.domain.RoomOccupation
import org.kkrasowski.cinema.domain.toRoomName

class ScheduleRepositoryStub : ScheduleRepository {

    private val occupations = mutableListOf<RoomOccupation>()

    override fun save(occupations: Collection<RoomOccupation>) {
        occupations.forEach { this.occupations.add(it) }
    }

    fun getSlotsForRoom(name: String): List<DateTimeSlot> = occupations
        .filter { it.roomName == name.toRoomName() }
        .map { it.slot }
}
