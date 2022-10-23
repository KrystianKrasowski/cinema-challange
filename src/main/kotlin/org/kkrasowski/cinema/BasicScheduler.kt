package org.kkrasowski.cinema

import java.time.LocalDateTime

class BasicScheduler(private val scheduleRepository: CinemaScheduleRepository) {

    fun schedule(film: Film, room: RoomName, startTime: LocalDateTime): Seance {
        return scheduleRepository
            .fetch()
            .schedule(film, room, startTime)
            .onScheduled { scheduleRepository.save(it) }
    }
}
