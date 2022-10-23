package org.kkrasowski.cinema.assertions

import org.assertj.core.api.AbstractAssert
import org.kkrasowski.cinema.Seance
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ScheduledSeanceAssert(seance: Seance.Scheduled) : AbstractAssert<ScheduledSeanceAssert, Seance.Scheduled>(seance, ScheduledSeanceAssert::class.java) {

    fun startsAt(dateTime: String): ScheduledSeanceAssert {
        val expectedStartsAt = dateTime.toLocalDateTime()

        if (actual.startsAt != expectedStartsAt) {
            failWithMessage("Expected seance start time to be <%s>, but was <%s>", expectedStartsAt, actual.startsAt)
        }

        return myself
    }

    fun endsAt(dateTime: String): ScheduledSeanceAssert {
        val expectedEndsAt = dateTime.toLocalDateTime()

        if (actual.endsAt != expectedEndsAt) {
            failWithMessage("Expected seance end time to be <%s>, but was <%s>", expectedEndsAt, actual.endsAt)
        }

        return myself
    }

    fun maintenanceStartsAt(dateTime: String): ScheduledSeanceAssert {
        val expectedStartsAt = dateTime.toLocalDateTime()

        if (actual.maintenanceStartsAt != expectedStartsAt) {
            failWithMessage("Expected maintenance start time to be <%s>, but was <%s>", expectedStartsAt, actual.maintenanceStartsAt)
        }

        return myself
    }

    fun maintenanceEndsAt(dateTime: String): ScheduledSeanceAssert {
        val expectedEndsAt = dateTime.toLocalDateTime()

        if (actual.maintenanceEndsAt != expectedEndsAt) {
            failWithMessage("Expected maintenance end time to be <%s>, but was <%s>", expectedEndsAt, actual.maintenanceEndsAt)
        }

        return myself
    }

    fun filmTitleIs(title: String): ScheduledSeanceAssert {
        if (actual.filmTitle != title) {
            failWithMessage("Expected film title to be <%s>, but was <%s>", title, actual.filmTitle)
        }
        return myself
    }

    private fun String.toLocalDateTime() = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
        .parse(this)
        .let { LocalDateTime.from(it) }
}
