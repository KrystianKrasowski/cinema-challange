package org.kkrasowski.cinema.spi

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import org.kkrasowski.cinema.domain.*
import java.time.Duration
import java.time.temporal.TemporalAmount

class ScheduleRepositoryStub : ScheduleRepository {

    private val rooms = mutableListOf<Room>()
    private val occupations = mutableListOf<RoomOccupation>()

    private var version: Long = 0
    private var openingHour: TemporalAmount = Duration.ofHours(0)
    private var closingHour: TemporalAmount = Duration.ofHours(0)
    private var premieresStartAt: TemporalAmount = Duration.ofHours(0)
    private var premieresEndAt: TemporalAmount = Duration.ofHours(0)
    private var failureReason: String? = null

    private val configuration
        get() = Configuration(openingHour, closingHour, premieresStartAt, premieresEndAt)

    override fun getSchedule(): CinemaSchedule {
        return CinemaSchedule(version, occupations.toList(), rooms.toList(), configuration)
    }

    override fun save(scheduledSeance: ScheduledSeance): Either<Failure, ScheduledSeance> {
        return failureReason
            ?.let { Failure(it) }
            ?.left()
            ?: storeAndReturn(scheduledSeance)
    }

    fun hasVersion(version: Long) = apply { this.version = version }

    fun getOccupations(): Collection<RoomOccupation> = occupations.toList()

    fun openingHourIs(hour: TemporalAmount) = apply { this.openingHour = hour }

    fun closingHourIs(hour: TemporalAmount) = apply { this.closingHour = hour }

    fun premieresStartAt(hour: TemporalAmount) = apply { this.premieresStartAt = hour }

    fun premieresEndAt(hour: TemporalAmount) = apply { this.premieresEndAt = hour }

    fun hasDefinedRoom(room: Room) = apply { rooms.add(room) }

    fun containsOccupation(occupation: RoomOccupation) = apply { occupations.add(occupation) }

    fun saveFailsDueTo(reason: String) = apply { this.failureReason = reason }

    /**
     * Added this private method due to strange -jvm-target IntelliJ error.
     * Even this error appeared, the builds were going just fine.
     */
    private fun storeAndReturn(scheduledSeance: ScheduledSeance) = scheduledSeance
        .also { this.occupations.addAll(it.occupations) }
        .right()
}
