package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.Room
import org.kkrasowski.cinema.domain.RoomName

class RoomsRepositoryStub : RoomsRepository {

    private val rooms = mutableMapOf<RoomName, Room>()

    override fun getByName(name: RoomName): Room {
        return rooms[name]!!
    }

    fun containsRoom(room: Room) = apply { rooms[room.name] = room }
}
