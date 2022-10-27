package org.kkrasowski.cinema

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAmount

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

        val requires3DGlasses: Boolean
            get() = film.displayType == Film.DisplayType.DISPLAY_3D

        val filmType: Film.FilmType
            get() = film.filmType

        val roomName: RoomName
            get() = room.name

        val dateTimeSlot: DateTimeSlot
            get() = DateTimeSlot(startsAt, maintenanceEndsAt)

        private val today
            get() = startsAt.truncatedTo(ChronoUnit.DAYS)

        fun clashesWith(seance: Scheduled): Boolean {
            return seance.dateTimeSlot.clashesWith(dateTimeSlot)
        }

        fun startsAfter(time: TemporalAmount): Boolean = today
            .plus(time)
            .let { startsAt >= it }

        fun endsBefore(time: TemporalAmount): Boolean = today
            .plus(time)
            .let { maintenanceEndsAt <= it }
    }

    object Declined : Seance()
}
