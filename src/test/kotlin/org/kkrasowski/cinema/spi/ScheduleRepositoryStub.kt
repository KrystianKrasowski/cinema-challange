package org.kkrasowski.cinema.spi

import org.kkrasowski.cinema.domain.*
import java.time.Duration
import java.time.temporal.TemporalAmount

class ScheduleRepositoryStub : ScheduleRepository {

    private val rooms = mutableListOf<Room>()
    private val occupations = mutableListOf<RoomOccupation>()

    private var version: Long = 1
    private var openingHour: TemporalAmount = Duration.ofHours(0)
    private var closingHour: TemporalAmount = Duration.ofHours(0)
    private var premieresStartAt: TemporalAmount = Duration.ofHours(0)
    private var premieresEndAt: TemporalAmount = Duration.ofHours(0)

    private val configuration
        get() = Configuration(openingHour, closingHour, premieresStartAt, premieresEndAt)

    override fun getSchedule(): CinemaSchedule {
        return CinemaSchedule(version, occupations.toList(), rooms.toList(), configuration)
    }

    override fun getOccupations(): Collection<RoomOccupation> = occupations.toList()

    override fun save(version: Long, occupations: Collection<RoomOccupation>) {
        occupations.forEach { this.occupations.add(it) }
    }

    fun getOccupationsForRoom(name: String): List<RoomOccupation> = occupations
        .filter { it.roomName == name.toRoomName() }
        .toList()

    fun openingHourIs(hour: TemporalAmount) = apply { this.openingHour = hour }

    fun closingHourIs(hour: TemporalAmount) = apply { this.closingHour = hour }

    fun premieresStarAt(hour: TemporalAmount) = apply { this.premieresStartAt = hour }

    fun premieresEndAt(hour: TemporalAmount) = apply { this.premieresEndAt = hour }

    fun hasDefinedRoom(room: Room) = apply { rooms.add(room) }

    fun containsOccupation(occupation: RoomOccupation) = apply { occupations.add(occupation) }
}
