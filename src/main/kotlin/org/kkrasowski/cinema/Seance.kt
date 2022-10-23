package org.kkrasowski.cinema

import java.time.LocalDateTime

sealed class Seance {

    fun onScheduled(block: (Scheduled) -> Unit): Seance {
        if (this is Scheduled) {
            block.invoke(this)
        }

        return this
    }

    data class Scheduled(private val room: Room, private val film: Film, private val startTime: LocalDateTime) : Seance() {
        val startsAt: LocalDateTime
            get() = startTime

        val endsAt: LocalDateTime
            get() = startTime + film.duration

        val maintenanceStartsAt: LocalDateTime
            get() = endsAt

        val maintenanceEndsAt: LocalDateTime
            get() = maintenanceStartsAt + room.maintenanceTime

        val filmTitle: String
            get() = film.title

        val roomName: RoomName
            get() = room.name

        fun clashesWith(seance: Scheduled): Boolean {
            return seance.maintenanceEndsAt >= this.startsAt && seance.maintenanceEndsAt <= this.maintenanceEndsAt
                    || seance.startsAt >= this.startsAt && seance.startsAt <= this.maintenanceEndsAt
        }
    }

    object Declined : Seance()
}
