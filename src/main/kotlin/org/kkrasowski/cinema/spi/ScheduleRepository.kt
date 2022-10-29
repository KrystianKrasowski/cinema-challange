package org.kkrasowski.cinema.spi

import arrow.core.Either
import org.kkrasowski.cinema.domain.CinemaSchedule
import org.kkrasowski.cinema.domain.Failure
import org.kkrasowski.cinema.domain.RoomOccupation
import org.kkrasowski.cinema.domain.ScheduledSeance

interface ScheduleRepository {

    fun getSchedule(): CinemaSchedule

    fun save(scheduledSeance: ScheduledSeance): Either<Failure, ScheduledSeance>
}
