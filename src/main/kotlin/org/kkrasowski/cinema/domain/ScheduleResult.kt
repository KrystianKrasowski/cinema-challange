package org.kkrasowski.cinema.domain

// TODO: Move SchedulResult to test scope
sealed class ScheduleResult {

    data class Success(val version: Long, val occupations: Collection<RoomOccupation>) : ScheduleResult()

    data class Failure(val reason: String) : ScheduleResult()
}

data class ScheduledSeance(val version: Long, val occupations: Collection<RoomOccupation>)

data class Failure(val reason: String)
