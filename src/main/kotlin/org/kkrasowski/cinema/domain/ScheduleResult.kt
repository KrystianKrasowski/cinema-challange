package org.kkrasowski.cinema.domain

data class ScheduledSeance(val version: Long, val occupations: Collection<RoomOccupation>)

data class Failure(val reason: String)
