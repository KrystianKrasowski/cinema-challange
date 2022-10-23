package org.kkrasowski.cinema

interface CinemaScheduleRepository {

    fun fetch(): CinemaSchedule

    fun save(seance: Seance.Scheduled)
}
