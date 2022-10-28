package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.Room
import org.kkrasowski.cinema.domain.RoomName

interface RoomsRepository {

    fun getByName(name: RoomName): Room
}
